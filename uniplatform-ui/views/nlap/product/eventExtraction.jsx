import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '事件抽取',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class EventExtraction extends Component {
    constructor(props){
        super(props);

        this.state = {
        }
        this.formGetter = this.formGetter.bind( this );
    }

    componentDidMount() {
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    handleValue() {
        this.setState({eventHtml:undefined});
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要抽取的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        fetch(Uniplatform.context.url + '/nlap/admin/event/extraction', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let event = data.data.map(function (item) {
                    let eve = {
                        contentSet : item.contentSet,
                        eventType : item.eventType
                    };
                    return eve;
                });
                this.setState({event});
                this.showEvent();
            }).catch((err) => console.log(err.toString()));
    }

    showEvent(){
        let {event} = this.state;
        let eventHtml = [];
        let color = ['#FFCC33','#FF99CC','#FF9999','#FFCC99','#336699','#FFFF66','#99CC66',
            '#666699','#ABCDEF','#99CC33','#FF9900','#99CC99','#FF0033','#FF9966','#CCFF00','#CC3399',
            '#FF6600','#FFFF99','#CCCCCC','#CC9999','#339999','#999966','#3399CC','#669999','#0099CC',
            '#CCCC99','#003366','#99CC00','#FFCCCC','#FFCC00','#CCFF99','#CC6600','#999999','#CCCC33',
            '#FF9933','#FFFFCC','#009933','#FFFF00','#CCFFCC','#CC6633','#99CCCC','#6699CC','#CC0066',
            '#009999','#CCFF66','#CCCCFF','#993333','#006699','#99CCFF','#0066CC','#339933','#66CC00'];
        let colorIndex = 0;
        for(let i=0; i<event.length; i++){
            if(i>color.length-1){
                colorIndex = i-color.length;
            }else {
                colorIndex = i;
            }
            eventHtml.push(
                <div key={i} style={{display:'inline-block',margin:'1%',padding:'6px',backgroundColor:color[colorIndex],
                    width:'22%',borderRadius:'5px',float:'left'}}>
                    <p style={{color:'#000',fontSize:'20px'}}>事件{i+1}</p>
                    <p style={{color:'#000'}}>
                        <span style={{fontSize:'14px',fontWeight:'600'}}>事件内容:</span>
                        {event[i].contentSet.map(function (item) {
                            return <span key="">{item}<br/></span>;
                        })}
                    </p>
                    <p style={{color:'#000'}}>
                        <span style={{fontSize:'14px',fontWeight:'600'}}>事件类型:</span>
                        {event[i].eventType}</p>
                </div>
            );
        }
        this.setState({eventHtml});
    }




    render() {
        let {eventHtml,show} = this.state;
        const contents = '[正文]资料图片：这是3月22日在比利时首都布鲁塞尔马埃勒贝克地铁站一个出口处拍摄的沾有血滴的地铁标志。' +
            '比利时联邦检察院22日发表声明称，当天上午在布鲁塞尔扎芬特姆机场和市区地铁站发生的爆炸是自杀式恐怖袭击。新华社记者叶平凡摄'+
            '参考消息网3月23日报道 美媒称，布鲁塞尔22日早晨发生一系列爆炸，造成几十人死亡数百人受伤。“伊斯兰国”组织声称制造的这些袭击事件' +
            '让这个恐怖之年变得更加血腥。2016年只有9天没有出现重大恐怖袭击事件。'+
            '据美国沃卡蒂夫新闻网站3月23日报道，今年前三个月，恐怖分子在世界上各个角落制造流血事件。据《2015年全球恐怖主义指数报告》显示，' +
            '无论是在阿富汗、巴基斯坦、叙利亚等传统上饱受恐怖主义折磨的国家，还是世界上的其他地方，暴力活动都大幅增加。'+
            '累计报道显示，截至2016年3月22日，全球各地今年至少有2200人死于恐怖主义活动。到目前为止，3月份只有1天没有被恐怖分子的袭击所破坏。' +
            '证实与伊斯兰教有关的恐怖袭击占迄今为止所有袭击案件的一半。'+
            '报道称，“伊斯兰国”组织或者怀疑由该组织制造的袭击事件占2016年迄今为止的所有恐怖袭击案件的20%。其中最大规模的一起是2月8日，' +
            '伊拉克有约300名警察和军事人员牺牲。在过去的81天中，伊拉克至少发生了34起恐怖袭击事件。迄今为止，恐怖袭击造成的最大伤亡是在中东和非洲。'+
            '报道称，除了22日在布鲁塞尔发生的恐怖袭击，当天声称制造这次袭击的“伊斯兰国”组织已经在埃及、利比亚、伊拉克、也门、俄罗斯、叙利亚、' +
            '沙特、土耳其和阿富汗制造过暴力活动。尽管法国1月份发生了“伊斯兰国”组织激励下的一次袭击事件，但布鲁塞尔的爆炸事件将是“伊斯兰国”' +
            '组织今年以来在欧洲制造的首次恐怖事件。今年，“伊斯兰国”组织声称制造的袭击事件总计将近40起。如果再算上“伊斯兰国”组织的分支或者受该' +
            '组织激励的群体，他们制造的袭击事件的数量几乎又增加了一倍。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/eventExtraction'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in10.png"/><span className="classTitle">事件抽取</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents} />
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>抽取</Button>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">抽取结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content" style={{maxHeight:'500px',overflow:'auto'}}>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 0px'}}>
                                    {show ?
                                        (eventHtml ? (eventHtml.length==0? <span>无结果</span> : eventHtml) : <Loading />)
                                        : null
                                    }
                                </Col>
                            </div>
                        </Row>
                    </Col>
                </Row>

                <Footer />
            </Page>
        );
    }

}
EventExtraction.UIPage = page;
export default EventExtraction;