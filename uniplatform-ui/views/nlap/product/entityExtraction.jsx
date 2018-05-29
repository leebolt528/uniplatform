import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '实体抽取',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class EntityExtraction extends Component {
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

    formGetterLanguage( getter ) {
        this.getValueSource = getter.value;
    }

    handleValue() {
        this.setState({wordHtml : undefined,natureHtml:undefined});
        const flag = this.getValueSource();
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要抽取的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        if(flag == 'chinese'){
            fetch(Uniplatform.context.url + '/nlap/admin/entity/extraction', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                        this.setState({show:false});
                    }else if (data.status != 200){
                        popup(<Snackbar message={data.msg}/>);
                        this.setState({show:false});
                    }
                    let words = [];
                    data.data.map(function (item) {
                        words.push({
                            word : item.word,
                            typeName : item.typeName
                        });
                    });
                    this.setState({words});
                    this.showEntity();
                }).catch((err) => console.log(err.toString()));
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/entity/extraction/en', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                        this.setState({show:false});
                    }else if (data.status != 200){
                        popup(<Snackbar message={data.msg}/>);
                        this.setState({show:false});
                    }
                    let words = [];
                    data.result.map(function (item) {
                        words.push({
                            word : item.name,
                            typeName : item.namedEntityTag
                        });
                    });
                    this.setState({words});
                    this.showEntity();
                }).catch((err) => console.log(err.toString()));
        }
    }

    //展示实体词
    showEntity(){
        let {words} = this.state;
        let color = ['#66CCCC','#FF99CC','#FF9999','#FF6666','#CC0066','#66CC00','#FFCC33','#666699',
            '#ABCDEF','#FF9900','#99CC99','#FF0033','#FF9966','#CC3399','#FF6600','#CCCCCC','#CC9999',
            '#999966','#3399CC','#669999','#CCCC99','#99CC00','#FFCCCC','#FFCC00','#CC6600','#999999',
            '#CCCC33','#FF9933','#009933','#FFFF00','#CCFFCC','#CC6633','#99CCCC','#6699CC','#009999',
            '#CCCCFF','#993333','#99CCFF','#0066CC','#339933'];
        let wordHtml = [], natureHtml = [];
        let all_natures = [];
        words.map(function (item) {
            all_natures.push(item.typeName);
        });
        let natures = [...new Set(all_natures)]

        for(let i=0; i<natures.length; i++){
            natureHtml.push(
                <div key={i} style={{display:'inline-block',margin:'2px 10px',padding:'4px',backgroundColor:color[i]}}
                     onClick={this.clickNature.bind(this,natures[i])}>
                    {natures[i]}
                </div>
            );
        }
        for(let i=0; i<words.length; i++){
            let colorIndex;
            natures.map(function (item,index) {
                if(item == words[i].typeName){
                    colorIndex = index;
                }
            });
            wordHtml.push(
                <div key={i} style={{display:'inline-block',margin:'0 10px',color:color[colorIndex]}}>
                    {words[i].word}
                </div>
            );
        }
        this.setState({wordHtml,natureHtml,natures,color});
    }

    //点击分类
    clickNature(typeName){
        let {natures,words,color} = this.state;
        let wordHtml = [];
        if(typeName == this.state.typeName){
            if(this.state.wordHtml.length == words.length){
                for(let i=0; i<words.length; i++){
                    if(words[i].typeName == typeName){
                        let colorIndex;
                        natures.map(function (item,index) {
                            if(item == words[i].typeName){
                                colorIndex = index;
                            }
                        });
                        wordHtml.push(
                            <div key={i} style={{display:'inline-block',margin:'0 10px',color:color[colorIndex]}}>
                                {words[i].word}
                            </div>
                        );
                    }
                }
            }else {
                for(let i=0; i<words.length; i++){
                    let colorIndex;
                    natures.map(function (item,index) {
                        if(item == words[i].typeName){
                            colorIndex = index;
                        }
                    });
                    wordHtml.push(
                        <div key={i} style={{display:'inline-block',margin:'0 10px',color:color[colorIndex]}}>
                            {words[i].word}
                        </div>
                    );
                }
            }
        }else {
            for(let i=0; i<words.length; i++){
                if(words[i].typeName == typeName){
                    let colorIndex;
                    natures.map(function (item,index) {
                        if(item == words[i].typeName){
                            colorIndex = index;
                        }
                    });
                    wordHtml.push(
                        <div key={i} style={{display:'inline-block',margin:'0 10px',color:color[colorIndex]}}>
                            {words[i].word}
                        </div>
                    );
                }
            }
        }
        this.setState({wordHtml,typeName});
    }




    render() {
        let {wordHtml,natureHtml,show} = this.state;
        const contents = '    诈骗的方式现在是多种多样，前不久有出现利用涉世不深的年轻人对偶像的热爱来诈骗获取钱财的手段。'+
            '2016年初，兴化的邹雨（化名）在给偶像刷票时遭遇骗局，损失一万多元。'+
            '邹雨今年刚刚大三，华南理工大学的学生，她和许多同龄人一样，喜欢追星。今年7月，某平台网上发起了一场“app粉丝节”的投票活动，' +
            '为了能让自己的偶像在投票中获胜，小邹决定花钱请“专业人士”为偶像刷票。小邹在网上通过QQ，找到了一位QQ号：352739573的刷票卖家，' +
            '自称是某平台这次活动的设计师，但是对方不同意通过网购的第三方平台交易，而是要让小邹把钱转账汇款到建设银行账号：6227000267090232680的；' +
            '并且还提出要先付钱之后才给刷票的要求。小邹起初不放心，要求留下对方电话号码，对方很爽快的留下了自己的电话：15611920818以及身份证号码：' +
            '330726196507040016 。在与这位“专业人士”讨价还价后，小邹决定以1万2千元来换取10万份投票数。'+
            '由于之前没有进行过类似交易，半信半疑的小邹先后分3次给对方转了500元、1000元和2000元。小邹说，在汇款的过程中，' +
            '她发现偶像的票数突然上涨了几百，这样她就打消了心中的疑惑。在将1万2千元全款转进相应账户后，小邹发现自己的偶像，得到的票数直到活动结束，' +
            '都没有明显增涨。小邹赶紧又联系对方，对方表示因为刷票不成功，到时候将统一退款。当小邹询问对方何时能退款的时候，“嚣张”的骗子竟然还嘲笑小邹' +
            '“都不知道自己被骗了”，并且还把小邹给拉黑了……'+
            '发现被骗后，不知所措的邹雨选择了报警，当地派出所民警立即与兴化市公安局网监大队取得联系，在网络中进行轨迹研判，经过缜密侦查，' +
            '办案民警发现中山市的陈某某重大嫌疑。当警方对陈某某实施抓捕行动的时候，发现嫌疑人经常乘坐的车辆“粤AEA369”已经在前往机场的路上，' +
            '准备飞往泰国。警察马上联系机场方面，根据机场提供的护照号：E23817750确定了嫌疑人的位置并实施了抓捕。最终，在广东当地警方的支持下，' +
            '陈某落入法网。陈某对违法事实供认不讳，并且退还了全部的赃款。'+
            '在这个诈骗案例中，其实我们应该想到，邹雨的行为从源头上来说，就是一种造假行为，也正是她这种迫切想为偶像出力的心态，让骗子钻了空子。' +
            '追星本来也无可厚非，如果小邹通过正常的渠道，而不是急功近利，也就不会被骗了。'+
            '----来自新浪新闻客户端，来稿请投news@sina.com';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/entityExtraction'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in3.png"/><span className="classTitle">实体抽取</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents}/>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>抽取</Button>
                            <Select value="chinese" showClear={false} getter={ this.formGetterLanguage.bind(this) }
                                    style={{float:'right',width:'20%',display:'inline-block',marginRight:'10px'}}>
                                <Option value="chinese">中文</Option>
                                <Option value="english">英文</Option>
                            </Select>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">抽取结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content" style={{maxHeight:'500px',overflow:'auto'}}>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    <span style={{fontSize:'12px'}}>
                                        {show ?
                                            (wordHtml ? (wordHtml.length==0? <span>无结果</span> : wordHtml) : <Loading />)
                                            : null
                                        }
                                    </span>
                                    <Divider line />
                                    <span style={{fontSize:'12px'}}>
                                        {show ? (natureHtml ? natureHtml : <Loading />) : null}
                                    </span>
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
EntityExtraction.UIPage = page;
export default EntityExtraction;