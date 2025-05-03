let stompClient = null;
	let connected = false;  // Track if the WebSocket is connected

	// Connect to WebSocket using SockJS
	// Connect to WebSocket using SockJS
function connectWebSocket() {
    const socket = new SockJS('/ws');  // This should match your server-side "/ws" endpoint
    stompClient = StompJs.Stomp.over(socket); 
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        connected = true;  // Set the connected flag to true after a successful connection
        subscribeToMessages();  // Subscribe to receive messages
    }, function (error) {
        console.error('Error connecting to WebSocket: ', error);
        connected = false;  // Set connected flag to false if connection fails
    });
}


	// Send a message using WebSocket
	function sendMessage() {
    const messageInput = document.getElementById("chatMessage");
    const messageContent = messageInput.value.trim();
    const senderUsername = document.getElementById("loggedInUser").textContent.trim();
    const receiverUsername = '0928666622'; // hoặc biến receiver nếu là dynamic

    if (!connected || !messageContent) return;

    const chatMessage = {
        senderUsername: senderUsername,
        receiverUsername: receiverUsername,
        content: messageContent
    };

    // ❌ KHÔNG append message tại đây nữa
    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
    messageInput.value = "";
}

	function subscribeToMessages() {
	    const receiverUsername = '0928666622'; // hoặc dùng biến nếu động

	    stompClient.subscribe(`/topic/messages/${receiverUsername}`, function (messageOutput) {
	        const message = JSON.parse(messageOutput.body);
	        const sender = document.getElementById("loggedInUser").textContent.trim();
	        const chatList = document.getElementById("chatList");

	        // Tạo thẻ <li> đúng theo format giống Thymeleaf
	        const li = document.createElement("li");
	        li.textContent = message.content;

	        // Gán class theo người gửi
	        if (message.senderUsername === sender) {
	            li.className = "chat-message me";
	        } else {
	            li.className = "chat-message user";
	        }

	        chatList.appendChild(li);
	        chatList.scrollTop = chatList.scrollHeight;
	    });
	}

	// Initialize WebSocket connection when the page loads
	window.onload = function() {
	    connectWebSocket();
	    subscribeToMessages();
	
	};

	// Show login modal if user is not authenticated
	function showLoginModal() {
	    const myModal = new bootstrap.Modal(document.getElementById('loginModal'));
	    myModal.show();
	}

	function toggleChatBox() {
	    const chatBox = document.getElementById("miniChatBox");
	    chatBox.style.display = chatBox.style.display === "none" || chatBox.style.display === "" ? "flex" : "none";
	}

	
	
	function showAnswer(element) {
	    const questionText = element.textContent;
	    let answerText = "";

	    // Define answers to the questions
	    switch (questionText) {
	        case "Xin chào! Bạn cần hỗ trợ gì?":
	            answerText = "Chúng tôi cung cấp các dịch vụ hỗ trợ trực tuyến, vui lòng đăng nhập để nhắn tin trực tuyến.";
	            break;
	        case "Giới thiệu sản phẩm của bạn":
	            answerText = "Đê biết thêm chi tiết cụ thể sản.";
	            break;
	        case "Thông tin thanh toán":
	            answerText = "Chúng tôi chấp nhận thanh toán qua nhiều phương thức, bao gồm thẻ tín dụng và chuyển khoản ngân hàng.";
	            break;
	        case "Cách sử dụng website":
	            answerText = "Bạn có thể tìm kiếm sản phẩm, thêm vào giỏ hàng và thanh toán trực tiếp trên website.";
	            break;
	        case "Liên hệ với chúng tôi":
	            answerText = "Bạn có thể liên hệ qua email usspharmaco3007@gmail.com hoặc gọi số hotline: 092 866 66 22";
	            break;
	        default:
	            answerText = "Xin lỗi, chúng tôi không có câu trả lời cho câu hỏi này.";
	    }

	    // Create the answer element
	    const answer = document.createElement("div");
	    answer.classList.add("chat-answer");
	    answer.textContent = answerText;

	    // Append the answer at the end of the chat body, not after the question
	    const chatBody = document.getElementById("chatBody");
	    chatBody.appendChild(answer);

	    // Ensure the new answer is visible
	    answer.style.display = "block";

	    // Scroll the chat body to the bottom
	    chatBody.scrollTop = chatBody.scrollHeight;
	}
