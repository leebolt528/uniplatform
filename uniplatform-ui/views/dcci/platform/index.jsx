import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
        Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner} from '../../../components/uniplatform-ui';


const contextUrl = '/uniplatform';

const page = {
    title: '管控首页',
    css: [
      '../css/singleBanner.min.css',
      '../css/controlIndex.min.css',
      '../css/index.min.css'
    ]
};

class controlIndex extends Component {
  constructor(props){
    super(props);

    this.state = {
        index:1,
        size:10,
        total:0,
        userList : [],
        visible:false,
        updateVisible:false,
        crawlerData: [],
        siteData: [],
        dateData: [],
        taskData: [],
        unsucTaskData:[]
    }
    this.getFetchStatistics = this.getFetchStatistics.bind(this);
      this.disabledStartDate = this.disabledStartDate.bind( this );
      this.disabledEndDate = this.disabledEndDate.bind( this );
  }

  componentDidMount() {
    this.getFetchStatistics();
    this.fetchCrawlerData();
    this.fetchSiteData();
    this.fetchDateData();
    this.fetchTaskData();
    this.fetchUnsucTaskData();
  }

    //统计机器中采集器数量
    fetchCrawlerData() {
        fetch(Uniplatform.context.url + '/dcci/statistics/count/crawler', {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                let crawlerData = [];
                data.data.map(function (item) {
                    crawlerData.push({"name": item.ip, "value": item.crawler_count});
                });
                this.setState({crawlerData});
                this.chart();
            }).catch((err) => console.log(err.toString()));
    }

    //统计采集器中站点数量
    fetchSiteData(){
        fetch(Uniplatform.context.url + '/dcci/statistics/count/site', {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                let siteData = [];
                data.data.map(function (item) {
                    let cname = item.crawler.split(' ');
                    siteData.push({"name": cname[0], "value": item.site_count});
                });
                this.setState({siteData});
                this.chart();
            }).catch((err) => console.log(err.toString()));
    }

    //采集员完成任务
    fetchTaskData(){
        this.switchTime();
        let newDate = new Date();
        let dd = newDate.switch('yyyy-MM-dd');
        //当前日期前一周时间
        let oneweekdate = new Date(newDate-7*24*3600*1000);
        let od = oneweekdate.switch('yyyy-MM-dd');
        let {sTime,eTime} = this.state;
        let startTime = sTime ? sTime : od;
        let endTime = eTime ? eTime : dd;
        this.setState({ startTime,endTime });
        fetch(Uniplatform.context.url + '/dcci/statistics/count/task/success?startTime='+startTime+'&endTime='+endTime , {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                let taskData = [];
                data.data.map(function (item) {
                    taskData.push({"name": item.userName+'('+item.account+')', "value": item.task_success_count});
                });
                this.setState({taskData});
                this.chart();
            }).catch((err) => console.log(err.toString()));
    }
    //采集员未完成任务
    fetchUnsucTaskData(){
        this.switchTime();
        let newDate = new Date();
        let dd = newDate.switch('yyyy-MM-dd');
        //当前日期前一周时间
        let oneweekdate = new Date(newDate-7*24*3600*1000);
        let od = oneweekdate.switch('yyyy-MM-dd');
        let {sTime,eTime} = this.state;
        let startTime = sTime ? sTime : od;
        let endTime = eTime ? eTime : dd;
        fetch(Uniplatform.context.url + '/dcci/statistics/count/task/unsuccess?startTime='+startTime+'&endTime='+endTime, {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                let unsucTaskData = [];
                data.data.map(function (item) {
                    unsucTaskData.push({"name": item.userName+'('+item.account+')', "value": item.task_unsuccess_count});
                });
                this.setState({unsucTaskData});
                this.chart();
            }).catch((err) => console.log(err.toString()));
    }

    //按日期统计总采集点
    fetchDateData(){
        this.switchTime();
        let newDate = new Date();
        let dd = newDate.switch('yyyy-MM-dd');
        //当前日期前一周时间
        let oneweekdate = new Date(newDate-7*24*3600*1000);
        let od = oneweekdate.switch('yyyy-MM-dd');
        let {sTime,eTime} = this.state;
        let startTime = sTime ? sTime : od;
        let endTime = eTime ? eTime : dd;
        fetch(Uniplatform.context.url + '/dcci/statistics/count/site/createdTime?startTime='+startTime+'&endTime='+endTime , {
            credentials: 'same-origin',
            method: 'GET'
        })
            .then((res) => res.json())
            .then((data) => {
                let dateData = [];
                data.data.map(function (item) {
                    dateData.push({"name": item.date, "value": item.site_count});
                });
                this.setState({dateData});
                this.chart();
            }).catch((err) => console.log(err.toString()));
    }

    switchTime(){
        // 扩充js的内置对象Date方法
        Object.assign(Date.prototype, {
            switch(time) {
                let date = {
                    "yy": this.getFullYear(),
                    // 这里月份的key采用大写，为了区别分钟的key
                    "MM": this.getMonth() + 1,
                    "dd": this.getDate(),
                    "hh": this.getHours(),
                    "mm": this.getMinutes(),
                    "ss": this.getSeconds()
                };
                //输出年 y+:匹配1个到多个y,i:忽略大小写
                if (/(y+)/i.test(time)) {
                    time = time.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
                }
                //输出月、日、时、分、秒
                Object.keys(date).forEach(function(i){
                    //  "(" + i + ")"的结果是字符串"(i+)",
                    // 只有写成"(" + i + ")"形式，才能在正则表达式中捕获子匹配，进而才能用到RegExp.$1的值
                    if (new RegExp("(" + i + ")").test(time)) {
                        // 判断，如果时间为一位数，则在前面加'0'
                        // ps：这里有一个小知识点：number类型+string类型 = string类型
                        if(RegExp.$1.length == 2){
                            date[i] < 10 ? date[i] = '0' + date[i]: date[i];
                        }
                        //替换初始化函数时候传入yyyy-mm-dd hh:mm:ss(这里可以打印出time、RegExp.$1、date[k])
                        time = time.replace(RegExp.$1, date[i]);
                    }
                })
                return time;
            }
        })
    }

    getFetchStatistics(){
        fetch( Uniplatform.context.url + '/dcci/statistics', {
            credentials: 'same-origin',
            method: 'post'
        } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let api = data.data.api_count;  //api
                let craw = data.data.craw_amount_count;  //采集量
                let crawler = data.data.crawler_count;  //采集器
                let exec_site = data.data.exec_site_count;  //执行采集点
                let exec_task = data.data.exec_task_count;  //执行任务
                let handle = data.data.handle_count;  //处理量
                let save = data.data.save_count;  //入库量
                let server = data.data.server_count;  //服务器
                let site = data.data.site_count; //总采集点
                let unassign = data.data.unassign_task_count;  //未分配任务

                this.setState({api,craw,crawler,exec_site,exec_task,handle,save,server,site,unassign});
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    startTime(time){
        let sTime = time;
        this.setState({sTime});
    }
    endTime(time){
        let eTime = time;
        this.setState({eTime});
    }

    disabledStartDate( startTime ) {
        const endDateString = this.state.endTime;
        const endDate = new Date(endDateString);
        return startTime.valueOf() > endDate.valueOf();
    }
    disabledEndDate( endTime ) {
        const startDateString = this.state.startTime;
        const startDate = new Date(startDateString).valueOf();
        return endTime.valueOf() <= startDate;
    }

    queryByTime(){
        this.fetchTaskData();
        this.fetchUnsucTaskData();
        this.fetchDateData();
    }

    chart(){
        let {dateData,taskData,unsucTaskData,crawlerData,siteData} = this.state;
        let dateBodyHtml = [],taskBodyHtml = [],unsucTaskBodyHtml = [],
            crawlerBodyHtml = [],siteBodyHtml = [];
        if(dateData.length > 0){
            dateBodyHtml.push(<Col size={{normal: 12, small: 24, medium: 12, large: 12}} key="">
                <h3 style={{color:'#3e8b68',fontSize:'17px'}}>总采集点</h3>
                <Chartbar data={dateData}/>
            </Col>);
        }
        if(taskData.length > 0){
            taskBodyHtml.push(<Col size={{normal: 12, small: 24, medium: 12, large: 12}} key="">
                <h3 style={{color:'#3e8b68',fontSize:'17px'}}>采集员完成任务</h3>
                <Chartbar data={taskData}/>
            </Col>);
        }
        if(unsucTaskData.length > 0){
            unsucTaskBodyHtml.push(<Col size={{normal: 12, small: 24, medium: 12, large: 12}} key="">
                <h3 style={{color:'#3e8b68',fontSize:'17px'}}>采集员未完成任务</h3>
                <Chartbar data={unsucTaskData}/>
            </Col>);
        }
        if(crawlerData.length > 0){
            crawlerBodyHtml.push(<Col size={{normal: 12, small: 24, medium: 12, large: 12}} key="">
                <h3 style={{color:'#3e8b68',fontSize:'17px'}}>机器中采集器数量</h3>
                <Chartbar data={crawlerData}/>
            </Col>);
        }
        if(siteData.length > 0){
            siteBodyHtml.push(<Col size={{normal: 12, small: 24, medium: 12, large: 12}} key="">
                <h3 style={{color:'#3e8b68',fontSize:'17px'}}>采集器中站点数量</h3>
                <Chartbar data={siteData} />
            </Col>);
        }
        this.setState({dateBodyHtml,taskBodyHtml,unsucTaskBodyHtml,crawlerBodyHtml,siteBodyHtml});
    }

  render() {
    let {api,craw,crawler,exec_site,exec_task,handle,save,server,site,unassign,dateBodyHtml,taskBodyHtml,
        unsucTaskBodyHtml,crawlerBodyHtml,siteBodyHtml,startTime,endTime} = this.state;
    return (
        <Page>
            <COMM_HeadBanner prefix="数据中心"/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/index'}/>
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>
                    <Row>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#fdc18d',textAlign:'center'}}>
                            <span className="contentUp-text">总采集点<p>{site}</p></span>
                        </Col>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#c6c1d2',textAlign:'center'}}>
                            <span className="contentUp-text">执行采集点<p>{exec_site}</p></span>
                        </Col>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#98d7ed',textAlign:'center'}}>
                            <span className="contentUp-text">服务器<p>{server}</p></span>
                        </Col>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#86b5a6',textAlign:'center'}}>
                            <span className="contentUp-text">采集器<p>{crawler}</p></span>
                        </Col>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#fbd195',textAlign:'center'}}>
                            <span className="contentUp-text">未分配任务<p>{unassign}</p></span>
                        </Col>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#7ec47b',textAlign:'center'}}>
                            <span className="contentUp-text">执行任务<p>{exec_task}</p></span>
                        </Col>
                        <Col className="contentUp" style={{width:'12%',backgroundColor:'#cbe3ff',textAlign:'center'}}>
                            <span className="contentUp-text">api<p>{api}</p></span>
                        </Col>
                    </Row>
                    <Divider line />
                    <Row>
                        <Col className="contentMid" style={{width:'28%',padding:'0px'}}>
                            <Col className="contentMid-img" style={{width:'40%'}}>
                                <img src="../image/p1.png"/>
                            </Col>
                            <Col style={{width:'60%'}}>
                                <h2 style={{textAlign:'center',marginTop:'20%',color:'green'}}>{craw}</h2>
                            </Col>
                        </Col>
                        <Col className="contentMid" style={{width:'28%',padding:'0px',margin:'2.6% 5%'}}>
                            <Col style={{width:'40%',backgroundColor:'rgb(175,209,255)',height:'100%'}}>
                                <img src="../image/p2.png"/>
                            </Col>
                            <Col style={{width:'60%'}}>
                                <h2 style={{textAlign:'center',marginTop:'20%',color:'purple'}}>{save}</h2>
                            </Col>
                        </Col>
                        <Col className="contentMid" style={{width:'28%',padding:'0px'}}>
                            <Col style={{width:'40%',backgroundColor:'rgb(255,202,114)',height:'100%'}}>
                                <img src="../image/p3.png"/>
                            </Col>
                            <Col style={{width:'60%'}}>
                                <h2 style={{textAlign:'center',marginTop:'20%',color:'darkorange'}}>{handle}</h2>
                            </Col>
                        </Col>
                    </Row>

                    <Row>
                        <Col size={{normal: 10, small: 24, medium: 10, large: 10}}>
                            <label>请选择开始时间：</label>
                            <DateTimePicker value={startTime} onChange={this.startTime.bind(this)} disabledDate={ this.disabledStartDate } style={{display:'inline-block',width:'70%'}}/>
                        </Col>
                        <Col size={{normal: 10, small: 24, medium: 10, large: 10}}>
                            <label>请选择结束时间：</label>
                            <DateTimePicker value={endTime} onChange={this.endTime.bind(this)} disabledDate={ this.disabledEndDate } style={{display:'inline-block',width:'70%'}}/>
                        </Col>
                        <Col size={{normal: 4, small: 24, medium: 4, large: 4}}>
                            <Button onClick={this.queryByTime.bind(this)}>查询</Button>
                        </Col>
                    </Row>
                    <Divider />

                    <Row>
                        {crawlerBodyHtml}
                        {siteBodyHtml}
                        {dateBodyHtml}
                        {taskBodyHtml}
                        {unsucTaskBodyHtml}
                    </Row>
                </Col>
              </Col>
            </Row>

            <Footer />
        </Page>
    );
  }

}
controlIndex.UIPage = page;
export default controlIndex;