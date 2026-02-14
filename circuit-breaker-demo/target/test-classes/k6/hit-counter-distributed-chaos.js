import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

const writesAttempted = new Counter("writes_attempted");
const writesSucceeded = new Counter("writes_succeeded");
const writesFailed = new Counter("writes_failed");
const writeSuccessRate = new Rate("write_success_rate");
const nodeDrift = new Trend("node_drift", true);

const baseA = __ENV.BASE_URL_A || "http://localhost:8080";
const baseB = __ENV.BASE_URL_B || "http://localhost:8081";
const counterKey = __ENV.COUNTER_KEY || "orders-k6-default";

export const options = {
  discardResponseBodies: true,
  scenarios: {
    distributed_hits: {
      executor: "ramping-arrival-rate",
      startRate: 100,
      timeUnit: "1s",
      preAllocatedVUs: 300,
      maxVUs: 2500,
      stages: [
        { duration: "20s", target: 100 },
        { duration: "30s", target: 1000 },
        { duration: "30s", target: 5000 },
        { duration: "20s", target: 1000 },
        { duration: "10s", target: 0 }
      ]
    }
  },
  thresholds: {
    http_req_failed: ["rate<0.30"],
    http_req_duration: ["p(95)<500"],
    write_success_rate: ["rate>0.70"]
  },
  summaryTrendStats: ["avg", "min", "med", "max", "p(90)", "p(95)", "p(99)"]
};

function chooseNode() {
  return (__VU + __ITER) % 2 === 0 ? baseA : baseB;
}

function getHits(url) {
  const response = http.get(`${url}/api/hit-counter/${counterKey}`, { tags: { req_type: "read" } });
  if (response.status !== 200) {
    return null;
  }
  try {
    return JSON.parse(response.body).hits;
  } catch (e) {
    return null;
  }
}

export default function () {
  const nodeUrl = chooseNode();
  writesAttempted.add(1);

  const writeResponse = http.post(`${nodeUrl}/api/hit-counter/${counterKey}/hit`, null, {
    tags: { req_type: "write" }
  });

  const writeOk = check(writeResponse, {
    "write status is 2xx": (r) => r.status >= 200 && r.status < 300
  });

  writeSuccessRate.add(writeOk);
  if (writeOk) {
    writesSucceeded.add(1);
  } else {
    writesFailed.add(1);
  }

  if (__ITER % 25 === 0) {
    const hitsA = getHits(baseA);
    const hitsB = getHits(baseB);
    if (hitsA !== null && hitsB !== null) {
      nodeDrift.add(Math.abs(hitsA - hitsB));
    }
  }

  sleep(0.001);
}
