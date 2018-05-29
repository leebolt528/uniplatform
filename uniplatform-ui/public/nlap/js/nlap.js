$(function(){
    /*$('.filter_span').delegate('a','click',function(){
        $(this).siblings('a').css('background','#fff');
        $(this).css('background','#dcdcdc');
    });*/

    /*var filter = $('.filter a');
    var length = filter.length;
    for(var i = 0; i < length; i++){
        filter[i].onclick = function () {
            for(var j = 0; j < length; j++){
                filter[j].style.backgroundColor = null;
            }
            this.style.backgroundColor = '#dcdcdc';
        }
    }*/

    var divs = $('.dicManage_content_col');
    var len = divs.length;
    for(var i = 0; i < len; i++){
        divs[i].onclick = function(){
            for(var j=0;j<len;j++){
                divs[j].style.backgroundColor = null;
                divs[j].style.border = null;
            }
            this.style.backgroundColor = "#ceeefc";
            this.style.border = "solid 1px #6fccf5";
        };
    }
});