const request = require("supertest");
const app = require("../../src/app");

describe("Posts API Contract Tests", () => {

    test("GET /api/posts should follow the posts response contract", async () => {
        const response = await request(app).get("/api/posts");

        expect(response.statusCode).toBe(200);
        expect(Array.isArray(response.body)).toBe(true);

        response.body.forEach((post) => {
            expect(post).toEqual(
                expect.objectContaining({
                    id: expect.any(Number),
                    title: expect.any(String),
                    status: expect.any(String)
                })
            );
        });
    });

    test("GET /api/posts/:id should follow the single post contract", async () => {
        const response = await request(app).get("/api/posts/1");

        expect(response.statusCode).toBe(200);

        expect(response.body).toEqual({
            id: expect.any(Number),
            title: expect.any(String),
            status: expect.any(String)
        });
    });

    test("POST /api/posts should follow the creation response contract", async () => {
        const response = await request(app)
            .post("/api/posts")
            .send({
                title: "Contract Testing"
            });

        expect(response.statusCode).toBe(201);

        expect(response.body).toEqual({
            id: expect.any(Number),
            title: expect.any(String),
            status: expect.any(String)
        });
    });

    test("Invalid post requests should follow the error response contract", async () => {
        const response = await request(app)
            .post("/api/posts")
            .send({
                title: ""
            });

        expect(response.statusCode).toBe(400);

        expect(response.body).toEqual({
            error: expect.any(String)
        });
    });

});