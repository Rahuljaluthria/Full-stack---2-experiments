const express = require("express");

const app = express();

app.use(express.json());

const posts = [
    {
        id: 1,
        title: "Introduction to CI/CD",
        status: "published"
    }
];

app.get("/", (req, res) => {
    res.status(200).json({
        message: "CI/CD Practical API is running"
    });
});

app.get("/api/posts", (req, res) => {
    res.status(200).json(posts);
});

app.get("/api/posts/:id", (req, res) => {
    const post = posts.find(
        (p) => p.id === Number(req.params.id)
    );

    if (!post) {
        return res.status(404).json({
            error: "Post not found"
        });
    }

    res.status(200).json(post);
});

app.post("/api/posts", (req, res) => {
    const { title } = req.body;

    if (!title || typeof title !== "string" || !title.trim()) {
        return res.status(400).json({
            error: "Valid title is required"
        });
    }

    const newPost = {
        id: posts.length + 1,
        title: title.trim(),
        status: "draft"
    };

    posts.push(newPost);

    res.status(201).json(newPost);
});

module.exports = app;