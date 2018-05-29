function relateWord(links){

    /*var links = [
        {source: "中国", target: "领衔主演", type: "resolved", rela:"相关性"},
        {source: "中国", target: "一定", type: "resolved", rela:"相关性"},
        {source: "中国", target: "新片", type: "resolved", rela:"相关性"},
        {source: "中国", target: "单打", type: "resolved", rela:"相关性"},
        {source: "中国", target: "互", type: "resolved", rela:"相关性"},
        {source: "领衔主演", target: "一定", type: "resolved",rela:"相关性"},
        {source: "领衔主演", target: "单打", type: "resolved",rela:"相关性"},
        {source: "领衔主演", target: "新片", type: "resolved", rela:"相关性"},
        {source: "领衔主演", target: "互", type: "resolved", rela:"相关性"},
        {source: "一定", target: "领衔主演", type: "resolved", rela:"相关性"},
        {source: "一定", target: "单打", type: "resolved", rela:"相关性"},
        {source: "一定", target: "小生", type: "resolved", rela:"相关性"},
        {source: "一定", target: "称", type: "resolved", rela:"相关性"},
        {source: "新片", target: "领衔主演", type: "resolved", rela:"相关性"},
        {source: "新片", target: "博士生", type: "resolved", rela:"相关性"},
        {source: "新片", target: "单打", type: "resolved", rela:"相关性"},
        {source: "新片", target: "安置", type: "resolved", rela:"相关性"},
        {source: "单打", target: "领衔主演", type: "resolved", rela:"相关性"},
        {source: "单打", target: "一定", type: "resolved", rela:"相关性"},
        {source: "单打", target: "这期", type: "resolved", rela:"相关性"},
        {source: "单打", target: "新片", type: "resolved", rela:"相关性"},
        {source: "互", target: "法国", type: "resolved", rela:"相关性"},
        {source: "互", target: "海外", type: "resolved", rela:"相关性"},
        {source: "互", target: "则", type: "resolved", rela:"相关性"},
        {source: "互", target: "任老师", type: "resolved", rela:"相关性"}
    ];*/

    var nodes = {};

    links.forEach(function(link) {
        link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
        link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
    });

    var width = 1000,
        height = 600;

    var force = d3.layout.force()//layout将json格式转化为力学图可用的格式
        .nodes(d3.values(nodes))//设定节点数组
        .links(links)//设定连线数组
        .size([width, height])//作用域的大小
        .linkDistance(100)//连接线长度
        .charge(-1000)//顶点的电荷数。该参数决定是排斥还是吸引，数值越小越互相排斥
        .on("tick", tick)//指时间间隔，隔一段时间刷新一次画面
        .start();//开始转换

    $('#chart').empty();
    var svg = d3.select("#chart").append("svg")
        .attr("width", width)
        .attr("height", height);

//箭头
    /* var marker=
     svg.append("marker")
     //.attr("id", function(d) { return d; })
     .attr("id", "resolved")
     //.attr("markerUnits","strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
     .attr("markerUnits","userSpaceOnUse")
     .attr("viewBox", "0 -5 10 10")//坐标系的区域
     .attr("refX",32)//箭头坐标
     .attr("refY", -1)
     .attr("markerWidth", 12)//标识的大小
     .attr("markerHeight", 12)
     .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
     .attr("stroke-width",2)//箭头宽度
     .append("path")
     .attr("d", "M0,-5L10,0L0,5")//箭头的路径
     .attr('fill','#000000');//箭头颜色
     */

//将连接线设置为曲线
    var path = svg.append("g").selectAll("path")
        .data(force.links())
        .enter().append("path")
        .attr("class", function(d) { return "link " + d.type; })
        .style("stroke",function(d){
            //console.log(d);
            return "#A254A2";//连接线的颜色
        })
        .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

//设置连接线
    var edges_line = svg.selectAll(".edgepath")
        .data(force.links())
        .enter()
        .append("path")
        .attr({
            'd': function(d) {return 'M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y},
            'class':'edgepath',
            //'fill-opacity':0,
            //'stroke-opacity':0,
            //'fill':'blue',
            //'stroke':'red',
            'id':function(d,i) {return 'edgepath'+i;}})
        .style("stroke",function(d){

            //根据关系的不同设置线条颜色
            var lineColor;
            if(d.source==links[0].source){
                lineColor="#8cbc4e";
            }else{
                lineColor="#f55967";
            }
            return lineColor;
        })
        .style("pointer-events", "none")
        .style("stroke-width",1)//线条粗细
    //.attr("marker-end", "url(#resolved)" );//根据箭头标记的id号标记箭头

    var edges_text = svg.append("g").selectAll(".edgelabel")
        .data(force.links())
        .enter()
        .append("text")
        .style("pointer-events", "none")
        //.attr("class","linetext")
        .attr({  'class':'edgelabel',
            'id':function(d,i){return 'edgepath'+i;},
            'dx':40,
            'dy':0
            //'font-size':10,
            //'fill':'#aaa'
        });

//设置线条上的文字
    edges_text.append('textPath')
        .attr('xlink:href',function(d,i) {return '#edgepath'+i})
        .style("pointer-events", "none")
        .text(function(d){return d.rela;});

//圆圈
    var circle = svg.append("g").selectAll("circle")
        .data(force.nodes())//表示使用force.nodes数据
        .enter().append("circle")
        .style("fill",function(node){
            var color;//圆圈背景色
            var link=links[node.index];
            if(node.name==links[0].source.name){
                color="#ffff99";
            }else{
                color="#F9EBF9";
            }
            return color;
        })
        .style('stroke',function(node){
            var color;//圆圈线条的颜色
            var link=links[node.index];
            if(node.name==links[0].source.name){
                color="#B43232";
            }else{
                color="#A254A2";
            }
            return color;
        })
        .attr("r", 28)//设置圆圈半径
        .on("click",function(node){
            //单击时让连接线加粗
            edges_line.style("stroke-width",function(line){
                console.log(line);
                if(line.source.name==node.name || line.target.name==node.name){
                    return 5;
                }else{
                    return 1;
                }
            });
            //d3.select(this).style('stroke-width',2);
        })
        .call(force.drag);//将当前选中的元素传到drag函数中，使顶点可以被拖动

    circle.append("text")
        .attr("dy", ".35em")
        .attr("text-anchor", "middle")//在圆圈内添加文字
        .text(function(d) {
            //console.log(d);
            return d.name;
        });

    //圆圈的提示文字
    circle.append("svg:title")
        .text(function(node) {
            /*var link=links[node.index];
             if(node.name==link.source.name && link.rela=="主营产品"){
             return "双击可查看详情"
             }*/
        });
    /* 矩形
     var rect=svg.append("rect")
     .attr({"x":100,"y":100,
     "width":100,"height":50,
     "rx":5,//水平圆角
     "ry":10//竖直圆角
     })
     .style({
     "stroke":"red",
     "stroke-width":1,
     "fill":"yellow"
     });*/
    var text = svg.append("g").selectAll("text")
        .data(force.nodes())
        //返回缺失元素的占位对象（placeholder），指向绑定的数据中比选定元素集多出的一部分元素。
        .enter()
        .append("text")
        .attr("dy", ".35em")
        .attr("text-anchor", "middle")//在圆圈中加上数据
        .style('fill',function(node){
            var color="#B43232";//文字颜色
            var link=links[node.index];
            /*if(node.name==link.source.name && link.rela=="主营产品"){
             color="#B43232";
             }else{
             color="#A254A2";
             }*/
            return color;
        }).attr('x',function(d){
            // console.log(d.name+"---"+ d.name.length);
            var re_en = /[a-zA-Z]+/g;
            //如果是全英文，不换行
            if(d.name.match(re_en)){
                d3.select(this).append('tspan')
                    .attr('x',0)
                    .attr('y',2)
                    .text(function(){return d.name;});
            }
            //如果小于四个字符，不换行
            else if(d.name.length<=4){
                d3.select(this).append('tspan')
                    .attr('x',0)
                    .attr('y',2)
                    .text(function(){return d.name;});
            }else{
                var top=d.name.substring(0,4);
                var bot=d.name.substring(4,d.name.length);

                d3.select(this).text(function(){return '';});

                d3.select(this).append('tspan')
                    .attr('x',0)
                    .attr('y',-7)
                    .text(function(){return top;});

                d3.select(this).append('tspan')
                    .attr('x',0)
                    .attr('y',10)
                    .text(function(){return bot;});
            }
            //直接显示文字
            /*.text(function(d) {
             return d.name; */
        });

    /*  将文字显示在圆圈的外面
     var text2 = svg.append("g").selectAll("text")
     .data(force.links())
     //返回缺失元素的占位对象（placeholder），指向绑定的数据中比选定元素集多出的一部分元素。
     .enter()
     .append("text")
     .attr("x", 150)//设置文字坐标
     .attr("y", ".50em")
     .text(function(d) {
     //console.log(d);
     //return d.name;
     //return d.rela;
     console.log(d);
     return  '1111';
     });*/

    function tick() {
        //path.attr("d", linkArc);//连接线
        circle.attr("transform", transform1);//圆圈
        text.attr("transform", transform2);//顶点文字
        //edges_text.attr("transform", transform3);
        //text2.attr("d", linkArc);//连接线文字
        //console.log("text2...................");
        //console.log(text2);
        //edges_line.attr("x1",function(d){ return d.source.x; });
        //edges_line.attr("y1",function(d){ return d.source.y; });
        //edges_line.attr("x2",function(d){ return d.target.x; });
        //edges_line.attr("y2",function(d){ return d.target.y; });

        //edges_line.attr("x",function(d){ return (d.source.x + d.target.x) / 2 ; });
        //edges_line.attr("y",function(d){ return (d.source.y + d.target.y) / 2 ; });


        edges_line.attr('d', function(d) {
            var path='M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y;
            return path;
        });

        edges_text.attr('transform',function(d,i){
            if (d.target.x<d.source.x){
                bbox = this.getBBox();
                rx = bbox.x+bbox.width/2;
                ry = bbox.y+bbox.height/2;
                return 'rotate(180 '+rx+' '+ry+')';
            }
            else {
                return 'rotate(0)';
            }
        });
    }

//设置连接线的坐标,使用椭圆弧路径段双向编码
    function linkArc(d) {
        //var dx = d.target.x - d.source.x,
        // dy = d.target.y - d.source.y,
        // dr = Math.sqrt(dx * dx + dy * dy);
        //return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
        //打点path格式是：Msource.x,source.yArr00,1target.x,target.y

        return 'M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y
    }
//设置圆圈和文字的坐标
    function transform1(d) {
        return "translate(" + d.x + "," + d.y + ")";
    }
    function transform2(d) {
        return "translate(" + (d.x) + "," + d.y + ")";
    }

}