function toggleChatbot() {
  let chatbot = document.getElementById("chatbot-container");
  chatbot.style.display = chatbot.style.display === "none" ? "block" : "none";
}

function sendMessage() {
  let inputField = document.getElementById("user-input");
  let message = inputField.value.trim();
  if (message === "") return;

  let chatbox = document.getElementById("chatbot-messages");
  chatbox.innerHTML += `<div><strong>Bạn:</strong> ${message}</div>`;

  // Gửi yêu cầu đến API chatbot
  fetch(`/api/chatbot/ask?question=${encodeURIComponent(message)}`)
    .then((response) => response.text())
    .then((data) => {
      chatbox.innerHTML += `<div><strong>Bot:</strong> ${data}</div>`;
      chatbox.scrollTop = chatbox.scrollHeight;
    });

  inputField.value = "";
}
