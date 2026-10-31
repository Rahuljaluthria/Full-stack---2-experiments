const {
    validatePostTitle,
    formatPost
} = require("../../src/utils/postUtils");

describe("Post Utility Unit Tests", () => {

    test("should accept a valid post title", () => {
        expect(validatePostTitle("CI/CD Pipeline")).toBe(true);
    });

    test("should reject a title shorter than 3 characters", () => {
        expect(validatePostTitle("AB")).toBe(false);
    });

    test("should reject an empty title", () => {
        expect(validatePostTitle("")).toBe(false);
    });

    test("should reject non-string titles", () => {
        expect(validatePostTitle(123)).toBe(false);
    });

    test("should format post details correctly", () => {
        const post = {
            id: 1,
            title: "  Automated Testing  ",
            status: "draft"
        };

        expect(formatPost(post)).toEqual({
            id: 1,
            title: "Automated Testing",
            status: "DRAFT"
        });
    });

});