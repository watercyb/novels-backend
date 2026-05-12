<?php
#Input: form-data -> token(String), user_id(long), image(file)
#Output: JSON -> {success, message, filename}

require("vn4309g9.php");

$allowed = ['image/jpeg'];

$token = $_POST['token'] ?? null;
$user_id = $_POST['user_id'] ?? null;
$file = $_FILES['image'] ?? null;

if ($token != $novel_token || $user_id == null || $file == null) {
    header('Content-Type: application/json');
    echo json_encode(["success" => "false", "message" => "Empty block"]);
    return;
}

$finfo = finfo_open(FILEINFO_MIME_TYPE);
$mime = finfo_file($finfo, $file['tmp_name']);

if (!in_array($mime, $allowed)) {
    header('Content-Type: application/json');
    echo json_encode(["success" => "false", "message" => "Invalid image type"]);
    return;
}

$dir = $user_id % 1000 . '/' . $user_id;
$path = 'files/' . $dir;
$filename = $path . '/avatar.jpg';
$filename_res = $dir . '/avatar.jpg';

if (!is_dir($path)) {
    if (!mkdir($path, 0777, true)) {
        header('Content-Type: application/json');
        echo json_encode(["success" => "false", "message" => "No users folder"]);
        return;
    }
}

if (!move_uploaded_file($file['tmp_name'], $filename)) {
    header('Content-Type: application/json');
    echo json_encode(["success" => "false", "message" => "Cannot create file"]);
} else {
    header('Content-Type: application/json');
    echo json_encode(["success" => "true", "filename" => $filename_res]);
}
