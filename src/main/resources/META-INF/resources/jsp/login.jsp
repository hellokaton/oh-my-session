<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>On-My_Session ⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄</title>
    <link href="http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #eee;
        }

        .form-signin {
            max-width: 330px;
            padding: 15px;
            margin: 0 auto;
        }
        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            margin-bottom: 10px;
        }
        .form-signin .checkbox {
            font-weight: normal;
        }
        .form-signin .form-control {
            position: relative;
            height: auto;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            padding: 10px;
            font-size: 16px;
        }
        .form-signin .form-control:focus {
            z-index: 2;
        }
        .form-signin input[type="email"] {
            margin-bottom: -1px;
            border-bottom-right-radius: 0;
            border-bottom-left-radius: 0;
        }
        .form-signin input[type="password"] {
            margin-bottom: 10px;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
        }
    </style>
    <script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/layer/3.0.1/layer.min.js"></script>
</head>
<body>

<div class="container">
    <form id="loginForm" class="form-signin">
        <h2 class="form-signin-heading">登录有惊喜(✪ω✪)</h2>
        <input type="text" class="form-control" name="username" placeholder="请输入用户名" required autofocus>
        <input type="password" class="form-control" name="password" placeholder="请输入密码" required>
        <button id="login-btn" class="btn btn-lg btn-primary btn-block" type="button">登录</button>
    </form>

</div> <!-- /container -->

<script type="text/javascript">
    $('#login-btn').click(function () {
        $.post('${uri}/login.json', $('#loginForm').serialize(), function (result) {
            if(result && result.success){
                window.location.href = '${uri}';
            } else{
                if(result && result.msg){
                    layer.alert(result.msg);
                }
            }
        });
    });
</script>
</body>
</html>