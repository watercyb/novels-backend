<?php
#Input: form-data -> token(String), user_id(long), novel_id(long), content(String), filename(String), remove(boolean)
#Output: JSON -> {success, message, filename}

require("vn4309g9.php");

$token = $_POST['token'] ?? null;
$user_id = $_POST['user_id'] ?? null;
$novel_id = $_POST['novel_id'] ?? null;
$content = $_POST['content'] ?? null;
$filename = $_POST['filename'] ?? null;
$remove = filter_var($_POST['remove'], FILTER_VALIDATE_BOOLEAN) ?? false;


if ($token != $novel_token || $user_id == null || $novel_id == null || $content == null) {
    header('Content-Type: application/json');
    echo json_encode(["success" => "false", "message" => "Invalid input"]);
} else {
    $dir = $user_id % 1000 . '/' . $user_id . '/' . $novel_id . '/chapters';
    $path = 'files/' . $user_id % 1000 . '/' . $user_id . '/' . $novel_id . '/chapters';

    if (!is_dir($path)) {
        if (!mkdir($path, 0777, true)) {
            header('Content-Type: application/json');
            echo json_encode(["success" => "false", "message" => "No chapters folder"]);
            return;
        }
    }

    if ($filename != null) {
        $name = $filename;
        $filename = $path . '/' . $name;
        $filename_res = $dir . '/' . $name;
        if (!file_exists($filename)) {
            header('Content-Type: application/json');
            echo json_encode(["success" => "false", "message" => "File not found"]);
            return;
        }
    } else {
        do {
            $name = random_int(100000000000, 999999999999);
            $filename = $path . '/' . $name . '.txt';
            $filename_res = $dir . '/' . $name . '.txt';
        } while (file_exists($filename));
        $remove == false;
    }

    if ($remove) {
        if (!unlink($filename)) {
            header('Content-Type: application/json');
            echo json_encode(["success" => "false", "message" => "Cannot delete file"]);
        } else {
            header('Content-Type: application/json');
            echo json_encode(["success" => "true", "filename" => ""]);
        }
    } else {
        if (!file_put_contents($filename, $content)) {
            header('Content-Type: application/json');
            echo json_encode(["success" => "false", "message" => "Cannot create file"]);
        } else {
            header('Content-Type: application/json');
            echo json_encode(["success" => "true", "filename" => $filename_res]);
        }
    }
}
