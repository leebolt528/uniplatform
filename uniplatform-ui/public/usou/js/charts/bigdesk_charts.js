/*
   Copyright 2011-2014 Lukas Vlcek

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

var bigdesk_charts = {
  default: {
    width: 270,
    height: 160
  }
};

bigdesk_charts.not_available = {

  chart: function (element) {
    return chartNotAvailable()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .svg(element).show();
  }
};

bigdesk_charts.fileDescriptors = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "File Descriptors",
        series1: "Open",
        series2: "Max",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.open_file_descriptors
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.max_file_descriptors
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#max_file_descriptors").text(nodeState.node.process.max_file_descriptors);
      $("#open_file_descriptors").text(nodeState.node.process.open_file_descriptors);
    }else {
      $("#max_file_descriptors").text('n/a');
      $("#open_file_descriptors").text('n/a');
    }
  }

};

bigdesk_charts.channels = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Channels",
        series1: "HTTP",
        series2: "Transport",
        margin_left: 5,
        margin_bottom: 6,
        width: 80
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: snapshot.node.http ? +snapshot.node.http.current_open : 0
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.transport.server_open
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#open_http_channels").text( nodeState.node.http ? nodeState.node.http.current_open : 0 );
      $("#open_transport_channels").text( nodeState.node.transport.server_open );
      $("#total_opened_http_channels").text( nodeState.node.http ? nodeState.node.http.total_opened : 0 );
    }else {
      $("#open_http_channels").text('n/a');
      $("#open_transport_channels").text('n/a');
      $("#total_opened_http_channels").text('n/a');
    }
  }

};

bigdesk_charts.jvmThreads = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Threads",
        series1: "Count",
        series2: "Peak",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.threads.count
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.threads.peak_count
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#jvm_threads_peak").text(nodeState.node.jvm.threads.peak_count);
      $("#jvm_threads_count").text(nodeState.node.jvm.threads.count);
    }else {
      $("#jvm_threads_peak").text("n/a");
      $("#jvm_threads_count").text("n/a");
    }
  }

};

bigdesk_charts.jvmHeapMem = {

  chart: function (element) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Heap Mem",
        series1: "Used",
        series2: "Committed",
        margin_left: 5,
        margin_bottom: 6,
        width: 85
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.mem.heap_used_in_bytes
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.mem.heap_committed_in_bytes
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#jvm_heap_mem_committed").text(nodeState.node.jvm.mem.heap_committed);
      $("#jvm_heap_mem_used").text(nodeState.node.jvm.mem.heap_used);
    }else {
      $("#jvm_heap_mem_committed").text("n/a");
      $("#jvm_heap_mem_used").text("n/a");
    }
  }

};

bigdesk_charts.jvmNonHeapMem = {

  chart: function (element) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Non-Heap Mem",
        series1: "Used",
        series2: "Committed",
        margin_left: 5,
        margin_bottom: 6,
        width: 85
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.mem.non_heap_used_in_bytes
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.mem.non_heap_committed_in_bytes
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#jvm_non_heap_mem_committed").text(nodeState.node.jvm.mem.non_heap_committed);
      $("#jvm_non_heap_mem_used").text(nodeState.node.jvm.mem.non_heap_used);
    }else {
      $("#jvm_non_heap_mem_committed").text("n/a");
      $("#jvm_non_heap_mem_used").text("n/a");
    }
  }
};

bigdesk_charts.jvmGC = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "GC (Δ)",
        series1: "Young gen count",
        series2: "Old gen count",
        series3: "Time both (sec)",
        margin_left: 5,
        margin_bottom: 6,
        width: 105
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.gc.collectors.young.collection_count
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +snapshot.node.jvm.gc.collectors.old.collection_count
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.jvm.timestamp,
        value: +(snapshot.node.jvm.gc.collectors.old.collection_time_in_millis + snapshot.node.jvm.gc.collectors.young.collection_time_in_millis) / 1000
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#jvm_gc_time").text(
        nodeState.node.jvm.gc.collectors.old.collection_time_in_millis + "ms / " + nodeState.node.jvm.gc.collectors.young.collection_time_in_millis + "ms"
      );
      $("#jvm_gc_count").text(
        nodeState.node.jvm.gc.collectors.old.collection_count + " / " + nodeState.node.jvm.gc.collectors.young.collection_count
      );
    }else {
      $("#jvm_gc_time").text("n/a");
      $("#jvm_gc_count").text("n/a");
    }
  }

};

bigdesk_charts.threadpoolSearch = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Search",
        series1: "Count",
        series2: "Peak",
        series3: "Queue",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.search.active
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.search.largest
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.search.queue
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#tp_search_count").text(nodeState.node.thread_pool.search.active);
      $("#tp_search_peak").text(nodeState.node.thread_pool.search.largest);
      $("#tp_search_queue").text(nodeState.node.thread_pool.search.queue);
    }else {
      $("#tp_search_count").text("n/a");
      $("#tp_search_peak").text("n/a");
      $("#tp_search_queue").text("n/a");
    }
  }

}

bigdesk_charts.threadpoolIndex = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Index",
        series1: "Count",
        series2: "Peak",
        series3: "Queue",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.index.active
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.index.largest
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.index.queue
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#tp_index_count").text(nodeState.node.thread_pool.index.active);
      $("#tp_index_peak").text(nodeState.node.thread_pool.index.largest);
      $("#tp_index_queue").text(nodeState.node.thread_pool.index.queue);
    }else {
      $("#tp_index_count").text("n/a");
      $("#tp_index_peak").text("n/a");
      $("#tp_index_queue").text("n/a");
    }
  }

}

bigdesk_charts.threadpoolBulk = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Bulk",
        series1: "Count",
        series2: "Peak",
        series3: "Queue",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.bulk.active
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.bulk.largest
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.bulk.queue
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#tp_bulk_count").text(nodeState.node.thread_pool.bulk.active);
      $("#tp_bulk_peak").text(nodeState.node.thread_pool.bulk.largest);
      $("#tp_bulk_queue").text(nodeState.node.thread_pool.bulk.queue);
    }else {
      $("#tp_bulk_count").text("n/a");
      $("#tp_bulk_peak").text("n/a");
      $("#tp_bulk_queue").text("n/a");
    }
  }

}

bigdesk_charts.threadpoolRefresh = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Refresh",
        series1: "Count",
        series2: "Peak",
        series3: "Queue",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.refresh.active
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.refresh.largest
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.thread_pool.refresh.queue
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#tp_refresh_count").text(nodeState.node.thread_pool.refresh.active);
      $("#tp_refresh_peak").text(nodeState.node.thread_pool.refresh.largest);
      $("#tp_refresh_queue").text(nodeState.node.thread_pool.refresh.queue);
    }else {
      $("#tp_refresh_count").text("n/a");
      $("#tp_refresh_peak").text("n/a");
      $("#tp_refresh_queue").text("n/a");
    }
  }

}

bigdesk_charts.osCpu = {

  chart: function (element) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "CPU (%)",
        series1: "Sys",
        series2: "User",
        margin_left: 5,
        margin_bottom: 6,
        width: 55
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: +snapshot.node.os.cpu.sys
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: (+snapshot.node.os.cpu.user + +snapshot.node.os.cpu.sys)
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: 100
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#os_cpu_user").text(nodeState.node.os.cpu.user + "%");
      $("#os_cpu_sys").text(nodeState.node.os.cpu.sys + "%");
    }else {
      $("#os_cpu_user").text("n/a");
      $("#os_cpu_sys").text("n/a");
    }
  }

};

bigdesk_charts.osMem = {

  chart: function (element) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Mem",
        series1: "Used",
        series2: "Free",
        margin_left: 5,
        margin_bottom: 6,
        width: 55
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: +snapshot.node.os.mem.used_in_bytes
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: ((+snapshot.node.os.mem.free_in_bytes) + (+snapshot.node.os.mem.used_in_bytes))
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#os_mem_free").text(nodeState.node.os.mem.free);
      $("#os_mem_used").text(nodeState.node.os.mem.used);
    }else {
      $("#os_mem_free").text("n/a");
      $("#os_mem_used").text("n/a");
    }
  }

};

bigdesk_charts.osSwap = {

  chart: function (element) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Swap",
        series1: "Used",
        series2: "Free",
        margin_left: 5,
        margin_bottom: 6,
        width: 55
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value:
        // https://github.com/elasticsearch/elasticsearch/issues/1804
          snapshot.node.os.swap.used_in_bytes == undefined ? 0 :
            +snapshot.node.os.swap.used_in_bytes
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: +snapshot.node.os.swap.free_in_bytes +
        // https://github.com/elasticsearch/elasticsearch/issues/1804
        ( snapshot.node.os.swap.used_in_bytes == undefined ? 0 :
          +snapshot.node.os.swap.used_in_bytes )
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#os_swap_free").text(nodeState.node.os.swap.free);
      $("#os_swap_used").text(
        nodeState.node.os.swap.used == undefined ? "n/a" :
          nodeState.node.os.swap.used
      );
    }else {
      $("#os_swap_free").text("n/a");
      $("#os_swap_used").text("n/a");
    }
  }

};

bigdesk_charts.osLoadAvg = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Load Average",
        series1: "1m",
        series2: "5m",
        series3: "15m",
        margin_left: 5,
        margin_bottom: 6,
        width: 40
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: +snapshot.node.os.cpu.load_average["1m"]
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: +snapshot.node.os.cpu.load_average["5m"]
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.os.timestamp,
        value: +snapshot.node.os.cpu.load_average["15m"]
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#os_load_0").text(nodeState.node.os.cpu.load_average["1m"]);
      $("#os_load_1").text(nodeState.node.os.cpu.load_average["5m"]);
      $("#os_load_2").text(nodeState.node.os.cpu.load_average["15m"]);
    }else {
      $("#os_load_0").text("n/a");
      $("#os_load_1").text("n/a");
      $("#os_load_2").text("n/a");
    }
  }

};

bigdesk_charts.indicesSearchReqs = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Search requests per second (Δ)",
        series1: "Fetch",
        series2: "Query",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.search.fetch_total
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.search.query_total
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_search_query_reqs").text(nodeState.node.indices.search.query_total);
      $("#indices_search_fetch_reqs").text(nodeState.node.indices.search.fetch_total);
    }else {
      $("#indices_search_query_reqs").text('n/a');
      $("#indices_search_fetch_reqs").text('n/a');
    }
  }

};

bigdesk_charts.indicesSearchTime = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Search time per second (Δ)",
        series1: "Fetch",
        series2: "Query",
        margin_left: 5,
        margin_bottom: 6,
        width: 60
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.search.fetch_time_in_millis
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.search.query_time_in_millis
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_search_query_time").text(nodeState.node.indices.search.query_time);
      $("#indices_search_fetch_time").text(nodeState.node.indices.search.fetch_time);
    }else {
      $("#indices_search_query_time").text('n/a');
      $("#indices_search_fetch_time").text('n/a');
    }
  }

};

bigdesk_charts.indicesGetReqs = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Get requests per second (Δ)",
        series1: "Missing",
        series2: "Exists",
        series3: "Get",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.get.total
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.get.missing_total
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.get.exists_total
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_get_reqs").text(nodeState.node.indices.get.total);
      $("#indices_exists_reqs").text(nodeState.node.indices.get.exists_total);
      $("#indices_missing_reqs").text(nodeState.node.indices.get.missing_total);
    }else {
      $("#indices_get_reqs").text('n/a');
      $("#indices_exists_reqs").text('n/a');
      $("#indices_missing_reqs").text('n/a');
    }
  }

};

bigdesk_charts.indicesGetTime = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Get time per second (Δ)",
        series1: "Missing",
        series2: "Exists",
        series3: "Get",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.get.time_in_millis
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.get.missing_time_in_millis
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.get.exists_time_in_millis
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_get_time").text(nodeState.node.indices.get.getTime);
      $("#indices_exists_time").text(nodeState.node.indices.get.exists_time);
      $("#indices_missing_time").text(nodeState.node.indices.get.missing_time);
    }else {
      $("#indices_get_time").text('n/a');
      $("#indices_exists_time").text('n/a');
      $("#indices_missing_time").text('n/a');
    }
  }

};

bigdesk_charts.indicesIndexingReqs = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Indexing requests per second (Δ)",
        series1: "Index",
        series2: "Delete",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.indexing.index_total
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.indexing.delete_total
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_indexing_delete_reqs").text(nodeState.node.indices.indexing.delete_total);
      $("#indices_indexing_index_reqs").text(nodeState.node.indices.indexing.index_total);
    }else {
      $("#indices_indexing_delete_reqs").text('n/a');
      $("#indices_indexing_index_reqs").text('n/a');
    }
  }

};

bigdesk_charts.indicesIndexingTime = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Indexing time per second (Δ)",
        series1: "Index",
        series2: "Delete",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.indexing.index_time_in_millis
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.indexing.delete_time_in_millis
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_indexing_delete_time").text(nodeState.node.indices.indexing.delete_time);
      $("#indices_indexing_index_time").text(nodeState.node.indices.indexing.index_time);
    }else {
      $("#indices_indexing_delete_time").text('n/a');
      $("#indices_indexing_index_time").text('n/a');
    }
  }

};

bigdesk_charts.indicesCacheSize = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Cache size",
        series1: "Field",
        series2: "Query",
        series3: "Request",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.fielddata.memory_size_in_bytes
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.query_cache.memory_size_in_bytes
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.request_cache.memory_size_in_bytes
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_query_cache_size").text(nodeState.node.indices.query_cache.memory_size);
      $("#indices_field_cache_size").text(nodeState.node.indices.fielddata.memory_size);
      $("#indices_request_cache_size").text(nodeState.node.indices.request_cache.memory_size);
    }else {
      $("#indices_query_cache_size").text("n/a");
      $("#indices_field_cache_size").text("n/a");
      $("#indices_request_cache_size").text("n/a");
    }
  }

};

bigdesk_charts.indicesCacheEvictions = {
  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Cache evictions (Δ)",
        series1: "Field",
        series2: "Query",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.fielddata.evictions
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.indices.query_cache.evictions
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#indices_filter_cache_evictions").text(nodeState.node.indices.query_cache.evictions);
      $("#indices_field_cache_evictions").text(nodeState.node.indices.fielddata.evictions);
    }else {
      $("#indices_filter_cache_evictions").text('n/a');
      $("#indices_field_cache_evictions").text('n/a');
    }
  }

};

bigdesk_charts.processCPU_time = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "CPU time (Δ)",
        series1: "User",
        series2: "Sys",
        margin_left: 5,
        margin_bottom: 6,
        width: 45
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.cpu.user_in_millis
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.cpu.sys_in_millis
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#process_cpu_time_sys").text(nodeState.node.process.cpu.sys_in_millis + "ms");
      $("#process_cpu_time_user").text(nodeState.node.process.cpu.user_in_millis + "ms");
    }else {
      $("#process_cpu_time_sys").text("n/a");
      $("#process_cpu_time_user").text("n/a");
    }
  }

};

bigdesk_charts.processCPU_pct = {

  chart: function (element, series2_label) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "CPU (%)",
        series1: "process",
        // series2: series2_label,
        series2: "Total",
        margin_left: 5,
        margin_bottom: 6,
        width: 65
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.cpu.percent
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.cpu.total_in_millis || 0
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      var totalCores = nodeState.node.os.cpu ? nodeState.node.os.cpu.total_cores : 1;
      $("#process_cpu_pct_total").text((totalCores * 100) + "%");
      $("#process_cpu_pct_process").text(nodeState.node.process.cpu.percent + "%");
    }else {
      $("#process_cpu_pct_total").text("n/a");
      $("#process_cpu_pct_process").text("n/a");
    }
  }

};

bigdesk_charts.processMem = {

  chart: function (element) {
    return timeAreaChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Mem",
        series1: "share",
        series2: "resident",
        series3: "total virtual",
        margin_left: 5,
        margin_bottom: 6,
        width: 100
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.mem.share_in_bytes || 0
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.mem.resident_in_bytes || 0
      }
    })
  },

  series3: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.process.timestamp,
        value: +snapshot.node.process.mem.total_virtual_in_bytes
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#process_mem_total_virtual").text(nodeState.node.process.mem.total_virtual);
      $("#process_mem_resident").text(nodeState.node.process.mem.resident || 0);
      $("#process_mem_share").text(nodeState.node.process.mem.share || 0);
    }else {
      $("#process_mem_total_virtual").text("n/a");
      $("#process_mem_resident").text("n/a");
      $("#process_mem_share").text("n/a");
    }
  }

};

bigdesk_charts.transport_txrx = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Transport size (Δ)",
        series1: "Tx",
        series2: "Rx",
        margin_left: 5,
        margin_bottom: 6,
        width: 40
      })
      .svg(element);
  },

  series1: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.transport.tx_size_in_bytes
      }
    })
  },

  series2: function (stats) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.timestamp,
        value: +snapshot.node.transport.rx_size_in_bytes
      }
    })
  },

  describe: function (nodeState, exist) {
    if (exist) {
      $("#transport_rx_size").text(nodeState.node.transport.rx_size);
      $("#transport_tx_size").text(nodeState.node.transport.tx_size);
      $("#transport_rx_count").text(nodeState.node.transport.rx_count);
      $("#transport_tx_count").text(nodeState.node.transport.tx_count);
    }else {
      $("#transport_rx_size").text("n/a");
      $("#transport_tx_size").text("n/a");
      $("#transport_rx_count").text("n/a");
      $("#transport_tx_count").text("n/a");
    }
  }

};

bigdesk_charts.disk_reads_writes_cnt = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Count of Reads & Writes (Δ)",
        series1: "Reads",
        series2: "Writes",
        margin_left: 5,
        margin_bottom: 6,
        width: 70
      })
      .svg(element);
  },

  series1: function (stats, fs_key) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.fs.timestamp,
        value: +snapshot.node.fs.io_stats.devices[fs_key].read_operations
      }
    })
  },

  series2: function (stats, fs_key) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.fs.timestamp,
        value: +snapshot.node.fs.io_stats.devices[fs_key].write_operations
      }
    })
  },

  describe: function (nodeState, key, exist) {
    if (exist) {
      $("#fs_disk_writes_"+ key +"").text(nodeState.node.fs.io_stats.devices[key].write_operations);
      $("#fs_disk_reads_"+ key +"").text(nodeState.node.fs.io_stats.devices[key].read_operations);
    }else {
      $("#fs_disk_writes_"+ key +"").text("n/a");
      $("#fs_disk_reads_"+ key +"").text("n/a");
    }
  }

};

bigdesk_charts.disk_reads_writes_size = {

  chart: function (element) {
    return timeSeriesChart()
      .width(bigdesk_charts.default.width).height(bigdesk_charts.default.height)
      .legend({
        caption: "Read & Write size (Δ)",
        series1: "Read",
        series2: "Write",
        margin_left: 5,
        margin_bottom: 6,
        width: 70
      })
      .svg(element);
  },

  series1: function (stats, fs_key) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.fs.timestamp,
        value: +snapshot.node.fs.io_stats.devices[fs_key].read_kilobytes
      }
    })
  },

  series2: function (stats, fs_key) {
    return stats.map(function (snapshot) {
      return {
        timestamp: +snapshot.node.fs.timestamp,
        value: +snapshot.node.fs.io_stats.devices[fs_key].write_kilobytes
      }
    })
  },

  describe: function (nodeState, key, exist) {
    if (exist) {
      $("#fs_disk_write_size_"+ key +"").text(nodeState.node.fs.io_stats.devices[key].write_kilobytes);
      $("#fs_disk_read_size_"+ key +"").text(nodeState.node.fs.io_stats.devices[key].read_kilobytes);
    }else {
      $("#fs_disk_write_size_"+ key +"").text("n/a");
      $("#fs_disk_read_size_"+ key +"").text("n/a");
    }
  }

};
