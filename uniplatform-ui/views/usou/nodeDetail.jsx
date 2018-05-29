import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Text,context,
   Snackbar,popup,Icon,Card,CardBody,D3Charts,d3} from 'epm-ui';
import { SingleBanner, util_formatTime, NodeChart, FsChart ,Footer,COMM_HeadBanner,UsouMenu,NavigationBar} from '../../components/uniplatform-ui';

const page = {
    title: 'Usou',
    css: [
      'css/clusters.min.css',
      'css/singleBanner.min.css',
      'css/charts/common.min.css',
      'css/charts/not-available-chart.min.css',
      'css/charts/time-area-chart.min.css',
      'css/charts/time-series-chart.min.css',
      'css/charts/pack.min.css',
        'css/leftnav.min.css'
    ],
    js: [
      '/bower_components/d3/d3.min.js',
      'js/charts/not-available/not-available-chart.min.js',
      'js/charts/time-area/time-area-chart.min.js',
      'js/charts/time-series/time-series-chart-copy.min.js',
      'js/charts/templates.min.js',
      'js/charts/bigdesk_charts.min.js'
    ]
};

class NodeDetail extends Component {
    constructor(props){
        super(props);
        this.state = {
            nodes:[],
            nodesStats:[],
            singleNode: {
              selectedNode: {},
              jvm: {},
              os: {},
              http: {},
              transport: {},
              indices: {}
              // fs: {}
            },
            allNodes: [],   // 维持20个时间点的数据
            node: null,       // 当前时间点数据
            exists: {}      // 标记对应chart是否存在数据
        }
        this.fetchNodeDetail = this.fetchNodeDetail.bind(this);
        this.getNodeStats = this.getNodeStats.bind(this);
        this.getSelectNode = this.getSelectNode.bind(this);
    }

    componentDidMount() {
        this.fetchNodeDetail();
    }

    // componentWillUnmount() {
    //   this.timer && clearTimeout(this.timer);
    // }

    fetchNodeDetail() {
        fetch( Uniplatform.context.url + '/usou/cluster/' + this.props.page.id + '/_node/' + this.props.page.nodeId + '/monitor', {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let { nodes, nodesStats } = data;
                this.setState( { nodes, nodesStats } );

                let singleNode = this.getSelectNode();
                this.setState( { singleNode } );

                // 处理状态数组信息
                let allNodes = this.state.allNodes;
                allNodes.length > 19 ?
                  allNodes.shift() : '';

                let nodeState = this.getNodeStats();
                if (nodeState != null) {
                  allNodes.push(nodeState[0]);
                  this.setState({
                    exists: {
                      osCpu: Boolean(nodeState[0].node.os.cpu && nodeState[0].node.os.cpu.sys),
                      osLoadAvg: Boolean(nodeState[0].node.os.cpu && nodeState[0].node.os.cpu.load_average),
                      processMem: Boolean(nodeState[0].node.process.mem),
                      processCPU_time: Boolean(nodeState[0].node.process.cpu.sys_in_millis),
                      transport_txrx: Boolean(nodeState[0].node.transport),
                      indicesSearchReqs: Boolean(nodeState[0].node.indices.search),
                      indicesGetReqs: Boolean(nodeState[0].node.indices.get),
                      indicesIndexingReqs: Boolean(nodeState[0].node.indices.indexing),
                      disk_reads_writes_cnt: Boolean( nodeState[0].node.fs.io_stats && (Object.keys(nodeState[0].node.fs.io_stats).length > 0) )
                    },
                    allNodes: allNodes,
                    node: nodeState[0]
                  });

                }

            }).catch( ( err ) => console.log( err.toString() ) );

            this.timer && clearTimeout(this.timer);
            this.timer = setTimeout( () => {
              this.fetchNodeDetail();
            }, 2000 );
    }

    // 获取节点状态信息
    getNodeStats() {
      let stats = this.state.nodesStats;

      return stats.nodes ?
        Object.keys(stats.nodes).map((nodeId) => {
          return {
            node: stats.nodes[nodeId]
          }
        })
        :
        null;
    }

    // 初始化节点的基本信息
    getSelectNode(){

        let { nodes, nodesStats } = this.state;

        return nodes.nodes ?
          Object.keys(nodes.nodes).map((indexNodeId) => {
            return {
              selectedNode: {
                name: nodes.nodes[indexNodeId].name,
                id: indexNodeId,
                hostName: nodes.nodes[indexNodeId].host,
                es: nodes.nodes[indexNodeId].version
              },
              jvm: {
                name: nodes.nodes[indexNodeId].jvm.vm_name,
                vendor: nodes.nodes[indexNodeId].jvm.vm_vendor,
                version: nodes.nodes[indexNodeId].jvm.vm_version,
                uptime: util_formatTime(nodesStats.nodes[indexNodeId].jvm.uptime_in_millis),
                java_version: nodes.nodes[indexNodeId].jvm.version,
                pId: nodes.nodes[indexNodeId].jvm.pid
              },
              os: {
                vendor: nodes.nodes[indexNodeId].os.vendor || 'n/a',
                model: nodes.nodes[indexNodeId].os.model || 'n/a',
                totalLogicalCores: nodes.nodes[indexNodeId].os.cores || 'n/a',
                cache: nodes.nodes[indexNodeId].os.cache || 'n/a',
                uptime: nodes.nodes[indexNodeId].os.uptime || 'n/a',
                refreshInterval: nodes.nodes[indexNodeId].os.refresh_interval_in_millis || 'n/a',
                totalMem: nodesStats.nodes[indexNodeId].os.mem.total || 'n/a',
                totalSwap: nodesStats.nodes[indexNodeId].os.swap.total || 'n/a'
              },
              http: {
                address: nodes.nodes[indexNodeId].http ? nodes.nodes[indexNodeId].http.http_address : '0.0.0.0.0',
                publishAddress: nodes.nodes[indexNodeId].http ? nodes.nodes[indexNodeId].http.publish_address : '0.0.0.0.0',
                boundAddress: nodes.nodes[indexNodeId].http ? nodes.nodes[indexNodeId].http.bound_address[0] : '0.0.0.0.0'
              },
              transport: {
                address: nodes.nodes[indexNodeId].transport.transport_address || '0.0.0.0.0',
                publishAddress: nodes.nodes[indexNodeId].transport.publish_address || '0.0.0.0.0',
                boundAddress: nodes.nodes[indexNodeId].transport.bound_address[0] || '0.0.0.0.0'
              },
              indices: {
                docsCount: nodesStats.nodes[indexNodeId].indices.docs.count || 'n/a',
                docsDeleted: nodesStats.nodes[indexNodeId].indices.docs.deleted || 'n/a',
                flushTime: nodesStats.nodes[indexNodeId].indices.flush.total_time || 'n/a',
                flushCount: nodesStats.nodes[indexNodeId].indices.flush.total || 'n/a',
                refreshTime: nodesStats.nodes[indexNodeId].indices.refresh.total_time || 'n/a',
                refreshCount: nodesStats.nodes[indexNodeId].indices.refresh.total || 'n/a',
                size: nodesStats.nodes[indexNodeId].indices.store.size || 'n/a'
              }
              // fs: {
              //   device: nodesStats.nodes[indexNodeId].fs.device || 'n/a',
              //   mount: nodesStats.nodes[indexNodeId].fs.data[0].mount || 'n/a',
              //   path: nodesStats.nodes[indexNodeId].fs.data[0].path || 'n/a',
              //   free: nodesStats.nodes[indexNodeId].fs.data[0].free || 'n/a',
              //   available: nodesStats.nodes[indexNodeId].fs.data[0].available || 'n/a',
              //   total: nodesStats.nodes[indexNodeId].fs.data[0].total || 'n/a'
              // }
            }
          })[0]
          :
          {};

    }

    render() {
        let {nodes, nodesStats, singleNode , allNodes, node} = this.state;
        let {id, nodeId} = this.props.page;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Divider/>
                <Row>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/nodeList'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                        <Row>
                            <NavigationBar code={'nodeDetail'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name} node={singleNode.selectedNode.name}/>
                        </Row>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                <Text>名称 : {singleNode.selectedNode.name}</Text>
                                <Text>ID : {singleNode.selectedNode.id}</Text>
                                <Text>主机 : {singleNode.selectedNode.hostName}</Text>
                                <Text>Elasticsearch 版本 : {singleNode.selectedNode.es}</Text>
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                <Card>
                                    <CardBody>
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>JVM</h3>
                                                <Col size={8}>虚拟机名称: {singleNode.jvm.name}</Col>
                                                <Col size={16}>运行时间: {singleNode.jvm.uptime}</Col>
                                                <Col size={8}>虚拟机供应商: {singleNode.jvm.vendor}</Col>
                                                <Col size={16}>Java 版本: {singleNode.jvm.java_version}</Col>
                                                <Col size={8}>虚拟机版本: {singleNode.jvm.version}</Col>
                                                <Col size={16}>进程号: {singleNode.jvm.pId}</Col>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="jvmHeapMem" stats={ allNodes } dataExist={true} number="2"/>
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="jvmNonHeapMem" stats={ allNodes } dataExist={true} number="2"/>
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="jvmThreads" stats={ allNodes } dataExist={true} number="2"/>
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="jvmGC" stats={ allNodes } dataExist={true} number="3" />
                                                    </Col>
                                                </Col>
                                            </Col>
                                        </Row>
                                        <Divider line={true}/>
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>线程池</h3>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="threadpoolSearch" stats={ allNodes } dataExist={true} number="3" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="threadpoolIndex" stats={ allNodes } dataExist={true} number="3" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="threadpoolBulk" stats={ allNodes } dataExist={true} number="3" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="threadpoolRefresh" stats={ allNodes } dataExist={true} number="3" />
                                                    </Col>
                                                </Col>
                                            </Col>
                                        </Row>
                                        <Divider line={true} />
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>操作系统</h3>
                                                <Col size={8}>CPU 供应商: { singleNode.os.vendor }</Col>
                                                <Col size={16}>运行时间: { singleNode.os.uptime }</Col>
                                                <Col size={8}>CPU 型号: { singleNode.os.model }</Col>
                                                <Col size={16}>刷新间隔: { singleNode.os.refreshInterval}ms</Col>
                                                <Col size={8}>CPU 总逻辑核数: { singleNode.os.totalLogicalCores }</Col>
                                                <Col size={16}>总计内存: { singleNode.os.totalMem }</Col>
                                                <Col size={8}>CPU 缓存: { singleNode.os.cache }</Col>
                                                <Col size={16}>总交换区大小: { singleNode.os.totalSwap }</Col>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="osCpu" stats={ allNodes } dataExist={ this.state.exists.osCpu } number="3" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="osMem" stats={ allNodes } dataExist={true} number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="osSwap" stats={ allNodes } dataExist={true} number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="osLoadAvg" stats={ allNodes } dataExist={this.state.exists.osLoadAvg} number="3" />
                                                    </Col>
                                                </Col>
                                            </Col>
                                        </Row>
                                        <Divider line={true}/>
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>进程</h3>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="fileDescriptors" stats={ allNodes } dataExist={ true } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="processMem" stats={ allNodes } dataExist={ this.state.exists.processMem } number="3"/>
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="processCPU_time" stats={ allNodes } dataExist={ this.state.exists.processCPU_time } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="processCPU_pct" stats={ allNodes } dataExist={ true } number="2" />
                                                    </Col>
                                                </Col>
                                            </Col>
                                        </Row>
                                        <Divider line={true}/>
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>HTTP & Transport</h3>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <Text>HTTP 地址: { singleNode.http.address }</Text>
                                                        <Text>HTTP 绑定地址: { singleNode.http.boundAddress }</Text>
                                                        <Text>HTTP 发布地址: { singleNode.http.publishAddress }</Text>
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <Text>Transport 地址: { singleNode.transport.address }</Text>
                                                        <Text>Transport 绑定地址: { singleNode.transport.boundAddress }</Text>
                                                        <Text>Transport 发布地址: { singleNode.transport.publishAddress }</Text>
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="channels" stats={ allNodes } dataExist={ true } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="transport_txrx" stats={ allNodes } dataExist={ this.state.exists.transport_txrx } number="2" />
                                                    </Col>
                                                </Col>
                                            </Col>
                                        </Row>
                                        <Divider line={true}/>
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>索引</h3>
                                                <Col size={6}>文档总数: { singleNode.indices.docsCount }</Col>
                                                <Col size={6}>Flush: {singleNode.indices.flushCount + ',' + singleNode.indices.flushTime}</Col>
                                                <Col size={12}>索引大小: { singleNode.indices.size }</Col>
                                                <Col size={6}>删除文档总数: { singleNode.indices.docsDeleted }</Col>
                                                <Col size={18}>刷新频率: { singleNode.indices.refreshCount + ',' + singleNode.indices.refreshTime }</Col>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesSearchReqs" stats={ allNodes } dataExist={ this.state.exists.indicesSearchReqs } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesSearchTime" stats={ allNodes } dataExist={ this.state.exists.indicesSearchReqs } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesGetReqs" stats={ allNodes } dataExist={ this.state.exists.indicesGetReqs } number="3" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesGetTime" stats={ allNodes } dataExist={ this.state.exists.indicesGetReqs } number="3" />
                                                    </Col>

                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesCacheSize" stats={ allNodes } dataExist={ true } number="3" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesCacheEvictions" stats={ allNodes } dataExist={ true } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesIndexingReqs" stats={ allNodes } dataExist={ this.state.exists.indicesIndexingReqs } number="2" />
                                                    </Col>
                                                    <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                        <NodeChart id="indicesIndexingTime" stats={ allNodes } dataExist={ this.state.exists.indicesIndexingReqs } number="2" />
                                                    </Col>
                                                </Col>
                                            </Col>
                                        </Row>
                                        <Divider line={true}/>
                                        <Row>
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                <h3 style={{ color: '#0055ff' }}>文件系统</h3>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                    {/* <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                                     <Text>Device: { singleNode.fs.device }</Text>
                                                     <Text>Mount: { singleNode.fs.mount }</Text>
                                                     <Text>Path: { singleNode.fs.path }</Text>
                                                     <Text>Free: { singleNode.fs.free }</Text>
                                                     <Text>Available: { singleNode.fs.available }</Text>
                                                     <Text>Total: { singleNode.fs.total }</Text>
                                                     </Col> */}
                                                    {
                                                        node ?
                                                            (( node.node.fs.io_stats && Object.keys( node.node.fs.io_stats ).length > 0 )
                                                                    ?
                                                                    node.node.fs.io_stats.devices.map( (item, index) => {
                                                                        return (
                                                                            <div key={ index }>
                                                                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                                                                    <Text>磁盘: { node.node.fs.io_stats.devices[index].device_name }</Text>
                                                                                    <Text>挂载点: { node.node.fs.data[index].mount }</Text>
                                                                                    <Text>数据路径: { node.node.fs.data[index].path }</Text>
                                                                                    <Text>剩余存储: { node.node.fs.data[index].free }</Text>
                                                                                    <Text>可用存储: { node.node.fs.data[index].available }</Text>
                                                                                    <Text>存储容量: { node.node.fs.data[index].total }</Text>
                                                                                </Col>
                                                                                <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                                                    <FsChart key={ 'fsChart_cnt_' + index } id={ 'fsChart_cnt_' + index } stats={ allNodes } dataExist={ this.state.exists.disk_reads_writes_cnt } chartName="disk_reads_writes_cnt" locIndex={ index } number="2" />
                                                                                </Col>
                                                                                <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                                                    <FsChart key={ 'fsChart_size_' + index } id={ 'fsChart_size_' + index } stats={ allNodes } dataExist={ this.state.exists.disk_reads_writes_cnt } chartName="disk_reads_writes_size" locIndex={ index } number="2" />
                                                                                </Col>
                                                                            </div>
                                                                        )
                                                                    } )
                                                                    :
                                                                    <div>
                                                                        <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                                                            <Text>磁盘: n/a</Text>
                                                                            <Text>挂载点: n/a</Text>
                                                                            <Text>数据路径: n/a</Text>
                                                                            <Text>剩余容量: n/a</Text>
                                                                            <Text>可用容量: n/a</Text>
                                                                            <Text>存储容量: n/a</Text>
                                                                        </Col>
                                                                        <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                                            <FsChart key='fsChart_cnt_0' id='fsChart_cnt_0' stats={ allNodes } dataExist={false} chartName="disk_reads_writes_cnt" locIndex='0' number="2" />
                                                                        </Col>
                                                                        <Col size={{ normal: 6, small: 12, medium: 6, large: 6 }}>
                                                                            <FsChart key='fsChart_size_0' id='fsChart_size_0' stats={ allNodes } dataExist={ false } chartName="disk_reads_writes_size" locIndex='0' number="2" />
                                                                        </Col>
                                                                    </div>
                                                            )
                                                            :
                                                            ''
                                                    }
                                                </Col>
                                            </Col>
                                        </Row>
                                    </CardBody>
                                </Card>
                            </Col>
                        </Row>
                    </Col>
                </Row>

                <Footer/>
            </Page>
        );
    }

}

NodeDetail.UIPage = page;
export default NodeDetail;
