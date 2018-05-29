import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu,ChartRadar} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '文本分类-文本',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ],
    js: [
        '../js/gauge.min.js',
        '../js/textCategorization.min.js'
    ]
};

class TextCategorization extends Component {
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

    //获取分类类别
    getClassifications() {
        this.setState({classifier:undefined});
        const flag = this.getValueSource();
        if(flag == 'chinese'){
            fetch(Uniplatform.context.url + '/nlap/admin/text/classify/classes?modelName=', {
                credentials: 'same-origin',
                method: 'POST'
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else if (data.status != 200){
                        popup(<Snackbar message={data.msg}/>);
                    }
                    let classifications = data.classes;
                    this.setState({classifications});
                    this.handleValue();
                }).catch((err) => console.log(err.toString()));
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/text/classify/classes/en?modelName=english14_14', {
                credentials: 'same-origin',
                method: 'POST'
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else if (data.status != 200){
                        popup(<Snackbar message={data.msg}/>);
                    }
                    let classifications = data.classes;
                    this.setState({classifications});
                    this.handleValue();
                }).catch((err) => console.log(err.toString()));
        }
    }

    handleValue() {
        let {classifications} = this.state;
        const flag = this.getValueSource();
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要分类的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        if(flag == 'chinese'){
            fetch(Uniplatform.context.url + '/nlap/admin/text/classify/text', {
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
                    let accuracy = (data.result[0].accuracy*100).toFixed(2) + '%';
                    let classifier = [];
                    let class_name = data.result[0].className;
                    let loc;
                    data.result.map(function (item) {
                        classifier.push({
                            className: item.className,
                            accuracy: item.accuracy.toFixed(4)
                        });
                    });
                    for(let i=0; i<classifications.length; i++){
                        if(classifications[i] == class_name){
                            loc = i*10;
                        }
                    }
                    gauge(classifications,loc,accuracy);
                    this.setState({classifier});
                }).catch((err) => console.log(err.toString()));
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/text/classify/text/en', {
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
                    let accuracy = (data.data[0].vate*100).toFixed(2) + '%';
                    let classifier = [];
                    let class_name = data.data[0].name;
                    let loc;
                    data.data.map(function (item) {
                        classifier.push({
                            className: item.name,
                            accuracy: item.vate.toFixed(4)
                        });
                    });
                    for(let i=0; i<classifications.length; i++){
                        if(classifications[i] == class_name){
                            loc = i*10;
                        }
                    }
                    gauge(classifications,loc,accuracy);
                    this.setState({classifier});
                }).catch((err) => console.log(err.toString()));
        }
    }




    render() {
        let {classifier,show} = this.state;
        const contents = '　　6月15日，“中国军网”英文网站刊发了一组解放军空降兵军直-10武装直升机训练照片。该组照片所配文字提到，' +
            '空降兵军某部武装直升机14日在吉林省公主岭市空军场站进行了飞行训练和实弹射击。而在上个月，央广军事《国防时空》栏目已报道过“北部战区空军空降兵某旅”这一番号。'+
            '综合多方消息来看，自原空降兵15军于今年4月下旬调整改编为“解放军空降兵军”以来，除原15军下辖师级部队全部改为旅级建制，' +
            '新空降兵军还从全国多个战区接收了转入的原陆军部队。其中，北部战区空军空降兵某旅就是由原陆军部队转隶、改编而来。' +
            '这使得解放军空降兵部队自组建后首次有下辖部队部署到我国东北省份。“中国军网”此次报道的武装直升机部队可能隶属于新组建的北部战区空降兵旅，' +
            '也可能是空降兵军直属直升机部队到公主岭市空军场站驻训。'+
            '公开资料显示，公主岭市空军场站原驻扎机型为强五强击机，如今强五强击机已全面退出现役，这个场站应该是被移交给了空降兵军，' +
            '成为空降兵直升机部队基地。由此推测，北部战区空降兵旅驻扎地也会在吉林省境内，甚至可能就在公主岭空军场站，毕竟该场站也适合起降固定翼运输机。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/textCategorization'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in8.png"/><span className="classTitle">文本分类-文本</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents}/>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.getClassifications.bind(this)}>分类</Button>
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
                                    <span className="dicManage_span">分类结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }} style={{padding:'30px',textAlign:'center'}}>
                                    <canvas id="gauge-ps" value={30}></canvas>
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }} style={{padding:'30px'}}>
                                    {show ? (classifier ? null : <Loading />) : null}
                                    {classifier ?
                                        <Table striped={true} multiLine={true} dataSource={classifier} bodyScrollable>
                                            <Column dataIndex="className" title="分类" scaleWidth='50%' textAlign="center"/>
                                            <Column dataIndex="accuracy" title="置信度" scaleWidth='50%' textAlign="center"/>
                                        </Table>
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
TextCategorization.UIPage = page;
export default TextCategorization;