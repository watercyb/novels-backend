<?php
#Input: form-data -> token(String), user_id(long), novel_id(long), image(file)
#Output: JSON -> {success, message, filename}

require("vn4309g9.php");


$allowed = ['image/jpeg'];

#$method = $_SERVER['REQUEST_METHOD'];

$token = $_POST['token'] ?? null;
$user_id = $_POST['user_id'] ?? null;
$novel_id = $_POST['novel_id'] ?? null;
$file = $_FILES['image'] ?? null;

if ($token != $novel_token || $user_id == null || $novel_id == null) {
    header('Content-Type: application/json');
    echo json_encode(["success" => "false", "message" => "Empty block"]);
    return;
}

$dir_res = $user_id % 1000 . '/' . $user_id . '/' . $novel_id;
$dir = 'files/' . $dir_res;

if ($file == null) {
    if (is_dir($dir)) {
        $filename = $dir . '/cover.jpg';
        if (file_exists($filename)) {
            if (unlink($filename)) {
                header('Content-Type: application/json');
                echo json_encode(["success" => "true", "filename" => '']);
                return;
            } else {
                header('Content-Type: application/json');
                echo json_encode(["success" => "false", "message" => "Cannot delete file"]);
            }
        }
    }
    header('Content-Type: application/json');
    echo json_encode(["success" => "true", "filename" => '']);
} else {
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mime = finfo_file($finfo, $file['tmp_name']);

    if (!in_array($mime, $allowed)) {
        header('Content-Type: application/json');
        echo json_encode(["success" => "false", "message" => "Invalid image type" .  $finfo]);
        return;
    }

    if (!is_dir($dir)) {
        if (!mkdir($dir, 0777, true)) {
            header('Content-Type: application/json');
            echo json_encode(["success" => "false", "message" => "No novels folder"]);
            return;
        }
    }

    $filename = $dir . '/cover.jpg';
    if (!move_uploaded_file($file['tmp_name'], $filename)) {
        header('Content-Type: application/json');
        echo json_encode(["success" => "false", "message" => "Cannot create file"]);
    } else {
        header('Content-Type: application/json');
        echo json_encode(["success" => "true", "filename" => $dir_res . '/cover.jpg']);
    }
}
