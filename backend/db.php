<?php
$host = "database-1.cdmyyy8405sb.us-east-1.rds.amazonaws.com";
$db   = "iot_db"; // O "mysql" según hayas creado
$user = "admin";
$pass = "matias13020"; // Cambia por tu contraseña de AWS RDS

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Error de conexión: " . $conn->connect_error]));
}
$conn->set_charset("utf8");
?>
