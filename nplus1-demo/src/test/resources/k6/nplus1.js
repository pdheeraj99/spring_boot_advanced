import http from 'k6/http';
import { check, sleep } from 'k6';

const base = __ENV.BASE_URL || 'http://localhost:8080';
const endpoint = __ENV.ENDPOINT || '/api/v1/orders/n-plus-one';
const vus = Number(__ENV.VUS || 50);
const iterations = Number(__ENV.ITERATIONS || 50);

export const options = {
  vus,
  iterations,
  thresholds: {
    http_req_failed: ['rate<0.1'],
  },
};

export default function () {
  const res = http.get(`${base}${endpoint}`);
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  sleep(0.05);
}
