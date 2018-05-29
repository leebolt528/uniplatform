/**
 * Created by lss on 2017/9/5.
 */
$(function () {

  // 构造数据
  var data1Chart1 = [
     // { timestamp: 1331840053059, value: 130 },
     // { timestamp: 1331840055024, value: 150 },
     // { timestamp: 1331840057038, value: 120 },
     // { timestamp: 1331840059029, value: 130 },
     // { timestamp: 1331840061040, value: 110 },
     // { timestamp: 1331840063046, value: 140 },
     // { timestamp: 1331840067051, value: 130 }
  ],
  data2Chart1 = [
    // { timestamp: 1331840053059, value: 120 },
    // { timestamp: 1331840055024, value: 140 },
    // { timestamp: 1331840057038, value: 110 },
    // { timestamp: 1331840059029, value: 120 },
    // { timestamp: 1331840061040, value: 100 },
    // { timestamp: 1331840063046, value: 130 },
    // { timestamp: 1331840067051, value: 120 }
  ],
  data3Chart1 = [
  ];

  var chart_jvmThreads = bigdesk_charts.jvmThreads.chart(d3.select("#svg_jvmThreads").attr('clip_id', 'clip_jvmThreads'));
  var chart_jvmHeapMem = bigdesk_charts.jvmHeapMem.chart(d3.select("#svg_jvmHeapMem").attr('clip_id', 'clip_jvmHeapMem'));
  var chart_jvmNonHeapMem = bigdesk_charts.jvmNonHeapMem.chart(d3.select("#svg_jvmNonHeapMem").attr('clip_id', 'clip_jvmNonHeapMem'));
  var chart_jvmGC = bigdesk_charts.jvmGC.chart(d3.select("#svg_jvmGC").attr('clip_id', 'clip_jvmGC'));

  var chart_threadpoolSearch = bigdesk_charts.threadpoolSearch.chart(d3.select("#svg_threadpoolSearch").attr('clip_id', 'clip_threadpoolSearch'));
  var chart_threadpoolIndex = bigdesk_charts.threadpoolIndex.chart(d3.select("#svg_threadpoolIndex").attr('clip_id', 'clip_threadpoolIndex'));
  var chart_threadpoolBulk = bigdesk_charts.threadpoolBulk.chart(d3.select("#svg_threadpoolBulk").attr('clip_id', 'clip_threadpoolBulk'));
  var chart_threadpoolRefresh = bigdesk_charts.threadpoolRefresh.chart(d3.select("#svg_threadpoolRefresh").attr('clip_id', 'clip_threadpoolRefresh'));

  var chart_osCpu = bigdesk_charts.osCpu.chart(d3.select("#svg_osCpu").attr('clip_id', 'clip_osCpu'));
  var chart_osMem = bigdesk_charts.osMem.chart(d3.select("#svg_osMem").attr('clip_id', 'clip_osMem'));
  var chart_osSwap = bigdesk_charts.osSwap.chart(d3.select("#svg_osSwap").attr('clip_id', 'clip_osSwap'));
  var chart_osLoadAvg = bigdesk_charts.osLoadAvg.chart(d3.select("#svg_osLoadAvg").attr('clip_id', 'clip_osLoadAvg'));

  // var chart_indicesSearchReqs = bigdesk_charts.indicesSearchReqs.chart(d3.select("#svg_indicesSearchReqs").attr('clip_id', 'clip_indicesSearchReqs'));
  // var chart_indicesSearchTime = bigdesk_charts.indicesSearchTime.chart(d3.select("#svg_indicesSearchTime").attr('clip_id', 'clip_indicesSearchTime'));
  // var chart_indicesGetReqs = bigdesk_charts.indicesGetReqs.chart(d3.select("#svg_indicesGetReqs").attr('clip_id', 'clip_indicesGetReqs'));
  // var chart_indicesGetTime = bigdesk_charts.indicesGetTime.chart(d3.select("#svg_indicesGetTime").attr('clip_id', 'clip_indicesGetTime'));
  // var chart_indicesIndexingReqs = bigdesk_charts.indicesIndexingReqs.chart(d3.select("#svg_indicesIndexingReqs").attr('clip_id', 'clip_indicesIndexingReqs'));
  // var chart_indicesCacheSize = bigdesk_charts.indicesCacheSize.chart(d3.select("#svg_indicesCacheSize").attr('clip_id', 'clip_indicesCacheSize'));
  // var chart_indicesCacheEvictions = bigdesk_charts.indicesCacheEvictions.chart(d3.select("#svg_indicesCacheEvictions").attr('clip_id', 'clip_indicesCacheEvictions'));
  // var chart_indicesIndexingTime = bigdesk_charts.indicesIndexingTime.chart(d3.select("#svg_indicesIndexingTime").attr('clip_id', 'clip_indicesIndexingTime'));
  // var chart_processCPU_time = bigdesk_charts.processCPU_time.chart(d3.select("#svg_processCPU_time").attr('clip_id', 'clip_processCPU_time'));

  var updateCharts = function () {

    // JVM Threads
    (function () {
      // 这里对请求参数进行处理 args：data1Chart1, data2Chart1

      try {
        chart_jvmThreads.animate(true).update(data1Chart1, data2Chart1);
      } catch (ignore) {

      }
    })();

    // --------------------------------------------
    // JVM Heap Mem
    (function () {

      try {
        chart_jvmHeapMem.animate(true).update(data1Chart1, data2Chart1);
      } catch (ignore) {

      }
    })();

    // --------------------------------------------
    // JVM Non Heap Mem
    (function() {
      try {
        chart_jvmNonHeapMem.animate(true).update(data1Chart1, data2Chart1);
      } catch (ignore) {
      }
    })();

    // --------------------------------------------
    // JVM GC
    (function() {
      try {
        chart_jvmGC.animate(true).update(data1Chart1, data2Chart1, data3Chart1);
      } catch (ignore) {
      }
    })();

    // --------------------------------------------
    // Threadpool Search
    (function () {
      try {
        chart_threadpoolSearch.animate(true).update(data1Chart1, data2Chart1, data3Chart1);
      } catch (ignore) {
      }
    })();

    // --------------------------------------------
    // Threadpool Index

    (function () {
      try {
        chart_threadpoolIndex.animate(true).update(data1Chart1, data2Chart1, data3Chart1);
      } catch (ignore) {
      }
    })();

    // --------------------------------------------
    // Threadpool Bulk

    (function () {
      try {
        chart_threadpoolBulk.animate(true).update(data1Chart1, data2Chart1, data3Chart1);
      } catch (ignore) {
      }
    })();

    // --------------------------------------------
    // Threadpool Refresh

    (function () {
      try {
        chart_threadpoolRefresh.animate(true).update(data1Chart1, data2Chart1, data3Chart1);
      } catch (ignore) {
      }
    })();

    // --------------------------------------------
    // OS CPU

    (function () {
      // sigar & AWS check
      isData = false;
      if (isData) {
        try {
          chart_osCpu.animate(true).update(data1Chart1, data2Chart1, data3Chart1);
        } catch (ignore) {
        }
      } else {
        chart_osCpu = bigdesk_charts.not_available.chart(chart_osCpu.svg());
      }
    })();

    // --------------------------------------------
    // OS Mem

    // _.defer(function () {
    //   // sigar & AWS check
    //   if (stats_the_latest && stats_the_latest.node && stats_the_latest.node.os && stats_the_latest.node.os.mem) {
    //
    //     var os_mem_actual_used = bigdesk_charts.osMem.series1(stats);
    //     var os_mem_actual_free = bigdesk_charts.osMem.series2(stats);
    //
    //     try {
    //       chart_osMem.animate(animatedCharts).update(os_mem_actual_used, os_mem_actual_free);
    //     } catch (ignore) {
    //     }
    //
    //     $("#os_mem_free").text(stats_the_latest.node.os.mem.actual_free);
    //     $("#os_mem_used").text(stats_the_latest.node.os.mem.actual_used);
    //   } else {
    //     chart_osMem = bigdesk_charts.not_available.chart(chart_osMem.svg());
    //     $("#os_mem_free").text("n/a");
    //     $("#os_mem_used").text("n/a");
    //   }
    // });

    // --------------------------------------------
    // OS swap

    // _.defer(function () {
    //   // sigar & AWS check
    //   if (stats_the_latest && stats_the_latest.node && stats_the_latest.node.os && stats_the_latest.node.os.swap) {
    //
    //     var os_swap_used = bigdesk_charts.osSwap.series1(stats);
    //     var os_swap_free = bigdesk_charts.osSwap.series2(stats);
    //
    //     try {
    //       chart_osSwap.animate(animatedCharts).update(os_swap_used, os_swap_free);
    //     } catch (ignore) {
    //     }
    //
    //     $("#os_swap_free").text(stats_the_latest.node.os.swap.free);
    //     $("#os_swap_used").text(
    //       // https://github.com/elasticsearch/elasticsearch/issues/1804
    //       stats_the_latest.node.os.swap.used == undefined ? "n/a" :
    //         stats_the_latest.node.os.swap.used
    //     );
    //   } else {
    //     chart_osSwap = bigdesk_charts.not_available.chart(chart_osSwap.svg());
    //     $("#os_swap_free").text("n/a");
    //     $("#os_swap_used").text("n/a");
    //   }
    // });

    // --------------------------------------------
    // OS load average

    // _.defer(function () {
    //   // sigar & AWS check
    //   if (stats_the_latest && stats_the_latest.node && stats_the_latest.node.os && stats_the_latest.node.os.load_average) {
    //
    //     var os_loadAvg_0 = bigdesk_charts.osLoadAvg.series1(stats);
    //     var os_loadAvg_1 = bigdesk_charts.osLoadAvg.series2(stats);
    //     var os_loadAvg_2 = bigdesk_charts.osLoadAvg.series3(stats);
    //
    //     try {
    //       chart_osLoadAvg.animate(animatedCharts).update(os_loadAvg_0, os_loadAvg_1, os_loadAvg_2);
    //     } catch (ignore) {
    //     }
    //
    //     $("#os_load_0").text(stats_the_latest.node.os.load_average["0"]);
    //     $("#os_load_1").text(stats_the_latest.node.os.load_average["1"]);
    //     $("#os_load_2").text(stats_the_latest.node.os.load_average["2"]);
    //   } else {
    //     chart_osLoadAvg = bigdesk_charts.not_available.chart(chart_osLoadAvg.svg());
    //     $("#os_load_0").text("n/a");
    //     $("#os_load_1").text("n/a");
    //     $("#os_load_2").text("n/a");
    //   }
    // });

  };

  updateCharts();

  setInterval(function () {
    var random1 = d3.random.normal(100, 30);
    var random2 = d3.random.normal(150, 20);
    var random3 = d3.random.normal(180, 40);

    if (data1Chart1.length > 19) {
      data1Chart1.shift();
      data2Chart1.shift();
      data3Chart1.shift();
    }
    var now = new Date().getTime();
    data1Chart1.push({ timestamp: now, value: random1() });
    data2Chart1.push({ timestamp: now, value: random2() });
    data3Chart1.push({ timestamp: now, value: random3() });

    updateCharts();
  }, 2000);

});
