/**
 * Created by lss on 2017/7/31.
 */
window.onload = function () {

  // 跨页面数据传递
  console.log( Uniplatform.args );

  console.log( Uniplatform.context.url );

  // 异步请求测试
  // $.get( Uniplatform.context.url + '/group/user', function (data) {
  //   console.log( data );
  // } )
};