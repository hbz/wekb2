<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Statistics</title>

</head>

<body>
<h1 class="ui header">Welcome to <g:message code="gokb.appname" default="we:kb"/></h1>

<h3 class="ui header">Statistics</h3>

<g:if test="${params.status == '404'}">
    <div class="alert alert-danger">
        The page you requested does not exist!
    </div>
</g:if>

<g:each var="component" in="${components.sort{it.key}}" status="count">

    <div id="chartMain${count}" style="width: 100%; height: 250px;"></div>
    <g:javascript>
    var option;

    option = {
      title: {
        text: <% print '"' + component.key + '"' %>
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: [<% print '"All ' + component.key + '",' + '"New ' + component.key + '"' %>]
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: [<% print '"' + xAxis.collect { it }.join('","') + '"' %>]
      },
      yAxis: {
        type: 'value'
      },
      series:[
           {
            name: <% print '"All ' + component.key + '"' %>,
            type: 'line',

            smooth:true,
            data: [<% print '"' + component.value.allComponentsData.collect { it }.join('","') + '"' %>]
        },
           {
            name: <% print '"New ' + component.key + '"' %>,
            type: 'line',

            smooth:true,
            data: [<% print '"' + component.value.newComponentsData.collect { it }.join('","') + '"' %>]
        }
      ]
    };

    var chartDom = document.getElementById("chartMain${count}");
    var myChart2 = echarts.init(chartDom);

    option && myChart2.setOption(option);

    </g:javascript>
  <br>
  <br>
  <br>
</g:each>

</body>
</html>
