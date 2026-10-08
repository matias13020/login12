<?php
header("Content-Type: application/json");
include 'db.php';

$data = json_decode(file_get_contents("php://input"), true);

$username = $data['username'] ?? '';
$password = $data['password'] ?? '';

if (empty($username) || empty($password)) {
    echo json_encode(["success" => false, "message" => "Campos vacíos"]);
    exit();
}

$stmt = $conn->prepare("INSERT INTO usuarios (username, password) VALUES (?, ?)");
$stmt->bind_param("ss", $username, $password);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Registro exitoso"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al registrar (usuario ya existe)"]);
}

$stmt->close();
$conn->close();
?>
