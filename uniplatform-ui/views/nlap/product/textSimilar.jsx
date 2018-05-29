import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '文本相似',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ],
    js: [
        '../js/gauge.min.js',
        '../js/dashboard.min.js'
    ]
};

class TextSimilar extends Component {
    constructor(props){
        super(props);

        this.state = {
            isShow : 'none',
            similarity : 3
        }
        this.formGetter_one = this.formGetter_one.bind(this);
        this.formGetter_two = this.formGetter_two.bind(this);
    }

    componentDidMount() {
    }

    formGetter_one( getter ) {
        this.getValueOne = getter.value;
    }

    formGetter_two( getter ) {
        this.getValueTwo = getter.value;
    }

    formGetterSort( getter ) {
        this.getValueSource = getter.value;
    }

    handleValue() {
        const flag = this.getValueSource();
        const text1 = this.getValueOne();
        const text2 = this.getValueTwo();
        if(!text1.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要对比的文本"/>);
            return;
        }else if(!text2.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要对比的文本"/>);
            return;
        }
        let param = new FormData();
        param.append('text1', text1);
        param.append('text2', text2);
        if(flag == 'text'){
            fetch(Uniplatform.context.url + '/nlap/admin/text/similarity/compare', {
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
                    let similarity = data.similarity;
                    gauge(similarity,'文本相似检测');
                    let isShow = 'inline';
                    this.setState({similarity,isShow});
                }).catch((err) => console.log(err.toString()));
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/text/similarity/compare/short', {
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
                    let similarity = (data.similarity*100).toFixed(2);
                    gauge(similarity,'语义相似检测');
                    let isShow = 'inline';
                    this.setState({similarity,isShow});
                }).catch((err) => console.log(err.toString()));
        }
    }




    render() {
        let {similarity,isShow} = this.state;
        const content1 = '　　Hadoop是一个由Apache基金会所开发的分布式系统基础架构。'+
            'Hadoop实现了一个分布式文件系统（Hadoop Distributed File System），简称HDFS。HDFS有高容错性的特点，并且设计用来部署在低廉的（low-cost）硬件上；而且它提供高吞吐量（high throughput）来访问应用程序的数据，适合那些有着超大数据集（large data set）的应用程序。'+
            'Hadoop不适合哪些场景，哪些场景适合？'+
            'Hadoop设计的目的主要包括下面几个方面，也就是所谓的适用场景：'+
            '1：超大文件'+
            '可以是几百M，几百T这个级别的文件。'+
            '2：流式数据访问'+
            'Hadoop适用于一次写入，多次读取的场景，也就是数据复制进去之后，长时间在这些数据上进行分析。'+
            '3：商业硬件'+
            '也就是说大街上到处都能买到的那种硬件，这样的硬件故障率较高，所以要有很好的容错机制。'+
            '接下来说说不适用的场景：'+
            '1： 低延迟数据访问'+
            'Hadoop设计的目的是大吞吐量，所以并没有针对低延迟数据访问做一些优化，如果要求低延迟， 可以看看Hbase。'+
            '2： 大量的小文件'+
            '由于NameNode把文件的MetaData存储在内存中，所以大量的小文件会产生大量的MetaData。这样的话百万级别的文件数目还是可行的，再多的话就有问题了。'+
            '3： 多用户写入，任意修改'+
            'Hadoop现在还不支持多人写入，任意修改的功能。也就是说每次写入都会添加在文件末尾。';
        const content2 = '　　Hadoop是一个由Apache基金会所开发的分布式系统基础架构。'+
            'Hadoop的框架最核心的设计就是：HDFS和MapReduce。HDFS为海量的数据提供了存储，则MapReduce为海量的数据提供了计算。'+
            '用户可以在不了解分布式底层细节的情况下，开发分布式程序。充分利用集群的威力进行高速运算和存储。'+
            'Hadoop不适合哪些场景？哪些场景适合？'+
            'Hadoop设计的目的主要包括下面几个方面，即所谓的适用场景：'+
            '1：超大文件'+
            '可以是几百M，几百T这个级别的文件。'+
            '2：流式数据访问'+
            'Hadoop适用于一次写入，多次读取的场景，也就是数据复制进去之后，长时间在这些数据上进行分析。'+
            '3：商业硬件'+
            '也就是说大街上到处都能买到的那种硬件，这样的硬件故障率较高，所以要有很好的容错机制。'+
            '接下来说说不适用的场景：'+
            '1： 低延迟数据访问'+
            'Hadoop设计的目的是大吞吐量，所以并没有针对低延迟数据访问做一些优化，如果要求低延迟， 可以看看Hbase。'+
            '2： 大量的小文件'+
            '由于NameNode把文件的MetaData存储在内存中，所以大量的小文件会产生大量的MetaData。这样的话百万级别的文件数目还是可行的，再多的话就有问题了。'+
            '3： 多用户写入，任意修改'+
            'Hadoop现在还不支持多人写入，任意修改的功能。也就是说每次写入都会添加在文件末尾。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/textSimilar'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in7.png"/><span className="classTitle">文本相似</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                <p style={{margin:'30px 0px 0px 30px',fontWeight:'600',color:'#70b4e4'}}>TEXT 1</p>
                                <Textarea style={{margin:'0px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                          getter={ this.formGetter_one } value={content1}/>
                            </Col>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                <p style={{margin:'30px 0px 0px 30px',fontWeight:'600',color:'#70b4e4'}}>TEXT 2</p>
                                <Textarea style={{margin:'0px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                          getter={ this.formGetter_two } value={content2}/>
                            </Col>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>检测</Button>
                            <Select value="text" showClear={false} getter={ this.formGetterSort.bind(this) }
                                    style={{float:'right',width:'20%',display:'inline-block',marginRight:'10px'}}>
                                <Option value="text">文本相似</Option>
                                <Option value="semantic">语义相似</Option>
                            </Select>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">检测结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{textAlign:'center',margin:'20px 23px',width:'95%'}}>
                                    <div className="container" style={{display:isShow}}>
                                        <canvas id="gauge-ps" value={similarity}></canvas>
                                    </div>
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
TextSimilar.UIPage = page;
export default TextSimilar;