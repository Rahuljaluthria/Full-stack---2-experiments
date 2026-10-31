function validatePostTitle(title) {
    return (
        typeof title === "string" &&
        title.trim().length >= 3
    );
}

function formatPost(post) {
    return {
        id: post.id,
        title: post.title.trim(),
        status: post.status.toUpperCase()
    };
}

module.exports = {
    validatePostTitle,
    formatPost
};