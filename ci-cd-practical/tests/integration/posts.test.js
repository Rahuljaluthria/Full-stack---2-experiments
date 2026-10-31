const request = require("supertest");
const app = require("../../src/app");

describe("Posts API Integration Tests", () => {

    test("GET / should return API running message", async () => {
        const response = await request(app).get("/");

        expect(response.statusCode).toBe(200);
        expect(response.body.message).toBe(
            "CI/CD Practical API is running"
        );
    });

    test("GET /api/posts should return all posts", async () => {
        const response = await request(app).get("/api/posts");

        expect(response.statusCode).toBe(200);
        expect(Array.isArray(response.body)).toBe(true);
        expect(response.body.length).toBeGreaterThan(0);
    });

    test("GET /api/posts/1 should return a specific post", async () => {
        const response = await request(app).get("/api/posts/1");

        expect(response.statusCode).toBe(200);
        expect(response.body.id).toBe(1);
        expect(response.body.title).toBe("Introduction to CI/CD");
    });

    test("GET /api/posts/999 should return 404", async () => {
        const response = await request(app).get("/api/posts/999");

        expect(response.statusCode).toBe(404);
        expect(response.body.error).toBe("Post not found");
    });

    test("POST /api/posts should create a new post", async () => {
        const response = await request(app)
            .post("/api/posts")
            .send({
                title: "Integration Testing"
            });

        expect(response.statusCode).toBe(201);
        expect(response.body.title).toBe("Integration Testing");
        expect(response.body.status).toBe("draft");
    });

    test("POST /api/posts should reject an invalid title", async () => {
        const response = await request(app)
            .post("/api/posts")
            .send({
                title: ""
            });

        expect(response.statusCode).toBe(400);
        expect(response.body.error).toBe("Valid title is required");
    });

});