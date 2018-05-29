function word(words,score){
    //Simple animated example of d3-cloud - https://github.com/jasondavies/d3-cloud
//Based on https://github.com/jasondavies/d3-cloud/blob/master/examples/simple.html


// Encapsulate the word cloud functionality
    function wordCloud(selector) {

        //输出20种类别的颜色 ---颜色比例尺
        var fill = d3.scale.category20();

        //Construct the word cloud's SVG element
        //append()使用函数在指定元素的结尾添加内容
        //transform:translate(x,y)  定义2d旋转，即平移，向右平移x,向下平移y
        $('#chart').empty();
        var svg = d3.select('#chart').append("svg")
            .attr("width", 500)
            .attr("height", 500)
            .append("g")
            .attr("transform", "translate(250,250)");

        //Draw the word cloud
        function draw(words) {
            var cloud = svg.selectAll("g text")
                .data(words, function(d) { return d.text; })

            //Entering words
            cloud.enter()
                .append("text")
                .style("font-family", "Impact")
                .style("fill", function(d, i) { return fill(i); })
                .attr("text-anchor", "middle")
                .attr('font-size', 1)
                .text(function(d) { return d.text; });

            //Entering and existing words
            cloud
                .transition()
                .duration(600)
                .style("font-size", function(d) { return d.size + "px"; })
                .attr("transform", function(d) {
                    return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
                })
                .style("fill-opacity", 1);

            //Exiting words
            cloud.exit()
                .transition()
                .duration(200)
                .style('fill-opacity', 1e-6)
                .attr('font-size', 1)
                .remove();


            //添加一个提示框
            var tooltip = d3.select("body")
                .append("div")
                .attr("class","tooltip")
                .style("opacity",0.0);

            cloud.on("mouseover",function(d){
                /*
                 鼠标移入时，
                 （1）通过 selection.html() 来更改提示框的文字
                 （2）通过更改样式 left 和 top 来设定提示框的位置
                 （3）设定提示框的透明度为1.0（完全不透明）
                 */

                var sco;
                for(var i=0; i<score.length; i++){
                    if(d.text == score[i].word){
                        sco = score[i].score;
                    }
                }

                tooltip.html("关键词："+d.text+ "<br />" + " 得分："+sco)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY + 20) + "px")
                    .style("opacity",1.0);
            })
                .on("mousemove",function(d){
                    /* 鼠标移动时，更改样式 left 和 top 来改变提示框的位置 */

                    tooltip.style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY + 20) + "px")
                        .style("position", "absolute");
                })
                .on("mouseout",function(d){
                    /* 鼠标移出时，将透明度设定为0.0（完全透明）*/

                    tooltip.style("opacity",0.0);
                });

        }


        //Use the module pattern to encapsulate the visualisation code. We'll
        // expose only the parts that need to be public.
        return {

            //Recompute the word cloud for a new set of words. This method will
            // asycnhronously call draw when the layout has been computed.
            //The outside world will need to call this function, so make it part
            // of the wordCloud return value.
            update: function(words) {
                d3.layout.cloud().size([500, 500])//size([x,y])  词云显示的大小
                    .words(words)
                    .padding(5)
                    //~~的作用是单纯的去掉小数部分，不论正负都不会改变整数部分
                    //这里的作用是产生0 1
                    .rotate(function() { return ~~(Math.random() * 2) * 90; })
                    .font("Impact")
                    .fontSize(function(d) { return d.size; })
                    .on("end", draw)//结束时运行draw函数
                    .start();
            }
        }

    }

//Some sample data - http://en.wikiquote.org/wiki/Opening_lines
    //var words = ["中国 是 以 华夏 文明 为 源泉、中华文化 为 基础 并 以 汉族 为 主体 民族 的 多民族 国家，通用 汉语。"]


//Prepare one of the sample sentences by removing punctuation,
// creating an array of words and computing a random size attribute.
    function getWords(i) {
        return words
            //.replace(/[!\.,:;\?]/g, '')
            //.split(' ')
            .map(function(d) {
                return {text: d, size: 10 + Math.random() * 60};
            })
    }

//This method tells the word cloud to redraw with a new set of words.
//In reality the new words would probably come from a server request,
// user input or some other source.
    function showNewWords(vis, i) {
        i = i || 0;


        vis.update(getWords(i ++ % words.length))
        //setTimeout(function() { showNewWords(vis, i + 1)}, 2000)
    }

//Create a new instance of the word cloud visualisation.
    var myWordCloud = wordCloud('body');

//Start cycling through the demo data
    showNewWords(myWordCloud);
}