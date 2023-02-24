<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <style>
    table{border-collapse:collapse;border-spacing:0;}
    td{padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
    th{;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
    </style>
</head>
<body>

<h1 class="ui header">Index Alert</h1>

<table>
        <thead>
        <tr>
            <th>index</th>
            <th>type</th>
            <th>count index</th>
            <th>diff DB and index</th>
            <th>count DB</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${indices.sort { it.type }}" var="indexInfo">
            <tr>
                <td>${indexInfo.name}</td>
                <td>${indexInfo.type}</td>
                <td>${indexInfo.countIndex}</td>
                <td>${indexInfo.countDB-indexInfo.countIndex}</td>
                <td>${indexInfo.countDB}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

</body>
</html>
