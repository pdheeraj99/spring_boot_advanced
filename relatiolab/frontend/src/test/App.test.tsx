import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import App from "../App";

vi.mock("../api/studentsApi", () => ({
  studentApi: {
    list: vi.fn().mockResolvedValue([]),
    create: vi.fn(),
    upsertProfile: vi.fn(),
    deleteProfile: vi.fn()
  }
}));

test("renders nav entries", async () => {
  render(<BrowserRouter><App /></BrowserRouter>);
  expect(await screen.findByRole("link", { name: "Students" })).toBeInTheDocument();
  expect(screen.getByRole("link", { name: "SQL Monitor" })).toBeInTheDocument();
});
