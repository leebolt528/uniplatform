var templates = {

  jvmHeapMem: [
      "<div>Committed: <span id='jvm_heap_mem_committed'>n/a</span></div>",
      "<div>Used: <span id='jvm_heap_mem_used'>n/a</span></div>"
  ].join(""),

  jvmNonHeapMem: [
    "<div>Committed: <span id='jvm_non_heap_mem_committed'>n/a</span></div>",
    "<div>Used: <span id='jvm_non_heap_mem_used'>n/a</span></div>"
  ].join(""),

  jvmThreads: [
      "<div>Peak: <span id='jvm_threads_peak'>n/a</span></div>",
      "<div>Count: <span id='jvm_threads_count'>n/a</span></div>"
  ].join(""),

  jvmGC: [
    "<div>Total time (O/Y): <span id='jvm_gc_time'>n/a</span></div>",
    "<div>Total count (O/Y): <span id='jvm_gc_count'>n/a</span></div>"
  ].join(""),

  threadpoolSearch: [
    "<div>Queue: <span id='tp_search_queue'>n/a</span></div>",
    "<div>Peak: <span id='tp_search_peak'>n/a</span></div>",
    "<div>Count: <span id='tp_search_count'>n/a</span></div>"
  ].join(""),

  threadpoolIndex: [
    "<div>Queue: <span id='tp_index_queue'>n/a</span></div>",
    "<div>Peak: <span id='tp_index_peak'>n/a</span></div>",
    "<div>Count: <span id='tp_index_count'>n/a</span></div>"
  ].join(""),

  threadpoolBulk: [
    "<div>Queue: <span id='tp_bulk_queue'>n/a</span></div>",
    "<div>Peak: <span id='tp_bulk_peak'>n/a</span></div>",
    "<div>Count: <span id='tp_bulk_count'>n/a</span></div>"
  ].join(""),

  threadpoolRefresh: [
    "<div>Queue: <span id='tp_refresh_queue'>n/a</span></div>",
    "<div>Peak: <span id='tp_refresh_peak'>n/a</span></div>",
    "<div>Count: <span id='tp_refresh_count'>n/a</span></div>"
  ].join(""),

  osCpu: [
    "<div>Total: 100%</div>",
    "<div>User: <span id='os_cpu_user'>n/a</span></div>",
    "<div>Sys: <span id='os_cpu_sys'>n/a</span></div>"
  ].join(""),

  osMem: [
    "<div>Free: <span id='os_mem_free'>n/a</span></div>",
    "<div>Used: <span id='os_mem_used'>n/a</span></div>"
  ].join(""),

  osSwap: [
      "<div>Free: <span id='os_swap_free'>n/a</span></div>",
      "<div>Used: <span id='os_swap_used'>n/a</span></div>"
  ].join(""),

  osLoadAvg: [
      "<div>15m: <span id='os_load_2'>n/a</span></div>",
      "<div>5m: <span id='os_load_1'>n/a</span></div>",
      "<div>1m: <span id='os_load_0'>n/a</span></div>"
  ].join(""),

  fileDescriptors: [
    "<div>Max: <span id='max_file_descriptors'>n/a</span></div>",
    "<div>Open: <span id='open_file_descriptors'>n/a</span></div>"
  ].join(""),

  processMem: [
      "<div>Total virtual: <span id='process_mem_total_virtual'>n/a</span></div>",
      "<div>Resident: <span id='process_mem_resident'>n/a</span></div>",
      "<div>Share: <span id='process_mem_share'>n/a</span></div>"
  ].join(""),

  processCPU_time: [
      "<!--#-->",
      "<div>Sys total: <span id='process_cpu_time_sys'>n/a</span></div>",
      "<div>User total: <span id='process_cpu_time_user'>n/a</span></div>"
  ].join(""),

  processCPU_pct: [
      "<div>Total: <span id='process_cpu_pct_total'>n/a</span></div>",
      "<div>Process: <span id='process_cpu_pct_process'>n/a</span></div>"
  ].join(""),

  channels: [
    "<div>Transport: <span id='open_transport_channels'>n/a</span></div>",
    "<div>HTTP: <span id='open_http_channels'>n/a</span></div>",
    "<div>HTTP total opened: <span id='total_opened_http_channels'>na</span></div>"
  ].join(""),

  transport_txrx: [
    "<!--#-->",
    "<div>Rx: <span id='transport_rx_size'>n/a</span>, #<span id='transport_rx_count'>n/a</span></div>",
    "<div>Tx: <span id='transport_tx_size'>n/a</span>, #<span id='transport_tx_count'>n/a</span></div>"
  ].join(""),

// -----------------------Indices------------------------------------

  indicesSearchReqs: [
    "Query: <span id='indices_search_query_reqs'>n/a</span>",
    "Fetch: <span id='indices_search_fetch_reqs'>n/a</span>"
  ].join("<br>"),

  indicesSearchTime: [
    "Query: <span id='indices_search_query_time'>n/a</span>",
    "Fetch: <span id='indices_search_fetch_time'>n/a</span>"
  ].join("<br>"),

  indicesGetReqs: [
      "Get: <span id='indices_get_reqs'>n/a</span>",
      "Exists: <span id='indices_exists_reqs'>n/a</span>",
      "Missing: <span id='indices_missing_reqs'>n/a</span>"
  ].join("<br>"),

  indicesGetTime: [
    "Get: <span id='indices_get_time'>n/a</span>",
    "Exists: <span id='indices_exists_time'>n/a</span>",
    "Missing: <span id='indices_missing_time'>n/a</span>"
  ].join("<br>"),

  indicesCacheSize: [
      "Query: <span id='indices_query_cache_size'>n/a</span>",
      "Request: <span id='indices_request_cache_size'>n/a</span>",
      "Field: <span id='indices_field_cache_size'>n/a</span>"
  ].join("<br>"),

  indicesCacheEvictions: [
      "Filter: <span id='indices_filter_cache_evictions'>n/a</span>",
      "Field: <span id='indices_field_cache_evictions'>n/a</span>"
  ].join("<br>"),

  indicesIndexingReqs: [
      "Delete: <span id='indices_indexing_delete_reqs'>n/a</span>",
      "Index: <span id='indices_indexing_index_reqs'>n/a</span>"
  ].join("<br>"),

  indicesIndexingTime: [
      "Delete: <span id='indices_indexing_delete_time'>n/a</span>",
      "Index: <span id='indices_indexing_index_time'>n/a</span>"
  ].join("<br>"),

  disk_reads_writes_cnt: function( key ) {
    return [
      "<div>Writes: <span id='fs_disk_writes_"+ key +"'>n/a</span></div>",
      "<div>Reads: <span id='fs_disk_reads_"+ key +"'>n/a</span></div>"
    ].join("");
  },

  disk_reads_writes_size: function( key ) {
    return [
        "<div>Write: <span id='fs_disk_write_size_"+ key +"'>n/a</span></div>",
        "<div>Read: <span id='fs_disk_read_size_"+ key +"'>n/a</span></div>"
    ].join("");
  }

}
