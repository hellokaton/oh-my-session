<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>On-My_Session监控管理</title>
    <!-- Bootstrap core CSS -->
    <link href="http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <style>
        /* Space out content a bit */
        body {
            padding-top: 20px;
            padding-bottom: 20px;
        }

        /* Everything but the jumbotron gets side spacing for mobile first views */
        .header,
        .marketing,
        .footer {
            padding-right: 15px;
            padding-left: 15px;
        }

        /* Custom page header */
        .header {
            border-bottom: 1px solid #e5e5e5;
        }
        /* Make the masthead heading the same height as the navigation */
        .header h3 {
            padding-bottom: 19px;
            margin-top: 0;
            margin-bottom: 0;
            line-height: 40px;
        }

        /* Custom page footer */
        .footer {
            padding-top: 19px;
            color: #777;
            border-top: 1px solid #e5e5e5;
        }

        /* Customize container */
        @media (min-width: 768px) {
            .container {
                max-width: 730px;
            }
        }
        .container-narrow > hr {
            margin: 30px 0;
        }

        /* Main marketing message and sign up button */
        .jumbotron {
            text-align: center;
            border-bottom: 1px solid #e5e5e5;
        }
        .jumbotron .btn {
            padding: 14px 24px;
            font-size: 21px;
        }

        /* Supporting marketing content */
        .marketing {
            margin: 40px 0;
        }
        .marketing p + h4 {
            margin-top: 28px;
        }

        /* Responsive: Portrait tablets and up */
        @media screen and (min-width: 768px) {
            /* Remove the padding we set earlier */
            .header,
            .marketing,
            .footer {
                padding-right: 0;
                padding-left: 0;
            }
            /* Space out the masthead */
            .header {
                margin-bottom: 30px;
            }
            /* Remove the bottom border on the jumbotron for visual effect */
            .jumbotron {
                border-bottom: 0;
            }
        }
    </style>

    <script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/layer/3.0.1/layer.min.js"></script>
</head>

<body>

<div class="container">
    <div class="header">
        <ul class="nav nav-pills pull-right" role="tablist">
            <li role="presentation" class="active"><a href="javascript:void(0)" class="logout">注销</a></li>
        </ul>
        <h3 class="text-muted">Oh-My_Session</h3>
    </div>

    <div class="jumbotron">
        <h2>监控数据</h2>
        <p class="lead">
            <ul>
                <li>session总数：<a href="javascript:void(0)" class="sessions">${requestScope.sessionCount}</a></li>
            </ul>
        </p>

        <p>
        <table class="table table-bordered table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>session key</th>
                <th>客户端ip</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody id="session-body">
            </tbody>
        </table>
        </p>
    </div>

    <%----%>
    <%--<div class="row marketing">--%>
        <%--<div class="col-lg-6">--%>
            <%--<h4>Subheading</h4>--%>
            <%--<p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>--%>

            <%--<h4>Subheading</h4>--%>
            <%--<p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>--%>

            <%--<h4>Subheading</h4>--%>
            <%--<p>Maecenas sed diam eget risus varius blandit sit amet non magna.</p>--%>
        <%--</div>--%>

        <%--<div class="col-lg-6">--%>
            <%--<h4>Subheading</h4>--%>
            <%--<p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>--%>

            <%--<h4>Subheading</h4>--%>
            <%--<p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>--%>

            <%--<h4>Subheading</h4>--%>
            <%--<p>Maecenas sed diam eget risus varius blandit sit amet non magna.</p>--%>
        <%--</div>--%>
    <%--</div>--%>

    <div class="footer">
        <p>&copy; On-My_Session 2017</p>
    </div>
</div> <!-- /container -->
<script type="text/javascript">
    $('.sessions').click(function () {
        $.getJSON('${uri}/sessions.json',{},function (result) {
            if(result && result.success){
                var list = result.payload;
                var htmlarr = [];
                for(var i in list){
                    var item = list[i];

                    htmlarr.push("<tr><td>"+(parseInt(i)+1)+"</td><td>"+item.id+"</td><td>"+item.host+"</td><td><a href=''>查看详情</a></td></tr>");
                }
                $("#session-body").html(htmlarr.join(''));
            } else{
                if(result && result.msg){
                    layer.alert(result.msg);
                }
            }
        });
    });

    $('.logout').click(function () {
        $.post('${uri}/logout.json', $('#loginForm').serialize(), function (result) {
            if(result && result.success){
                window.location.reload();
            }
        });
    });
</script>
</body>
</html>
