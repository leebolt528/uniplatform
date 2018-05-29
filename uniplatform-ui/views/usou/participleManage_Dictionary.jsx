import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Text,Pagination,context,Modal,ModalHeader,ModalBody,
    ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,Checkbox,Dialog,Option,Select,Upload,Tabs,Tab,ButtonGroup,
    Container,Textarea,Progress} from 'epm-ui';
import { SingleBanner,Footer,COMM_HeadBanner,UsouMenu,NavigationBar } from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'Usou',
    css: [
        'css/clusters.min.css',
        'css/singleBanner.min.css',
        'css/leftnav.min.css'
    ]
};

class participleManage extends Component {
    constructor(props){
        super(props);
        this.state = {
            data:[],
            visible: false,
            uploadVisible:false,
            alterVisible:false,
            select:null,
            id:0,
            dic:"dic",
            indexData:null,
            typeData:null,
            fieldData:null,
            typesFields:null,
            tokens:[],
            tokensText:[],
            deleteWord:null,
            index:1,
            total:0,
            size:10,
            searchData:[],
            progress_bool: false,
            dataAll:[],
            searchBool:false
        };

        this.addWord=this.addWord.bind(this);
        this.uploadWord=this.uploadWord.bind(this);
        this.confirmBatchDelete=this.confirmBatchDelete.bind(this);
        this.batchDelete=this.batchDelete.bind(this);
        this.alterWord=this.alterWord.bind(this);
        this.confirmDelete=this.confirmDelete.bind(this);
        this.deleteWord=this.deleteWord.bind(this);
        this.handleSelect=this.handleSelect.bind(this);
        this.handleValue=this.handleValue.bind(this);
        this.formTirgger=this.formTirgger.bind(this);
        this.formGetter=this.formGetter.bind(this);
        this.handleClose=this.handleClose.bind(this);
        this.noSelect=this.noSelect.bind(this);

        this.addHandleSubmit=this.addHandleSubmit.bind(this);
        this.handleResponse=this.handleResponse.bind(this);
        this.formTirggerWord=this.formTirggerWord.bind(this);
        this.loadAnalyzerData=this.loadAnalyzerData.bind(this);
        this.formGetterAnalyzer=this.formGetterAnalyzer.bind(this);
        this.formGetterInput=this.formGetterInput.bind(this);
        this.formGetterIndex=this.formGetterIndex.bind(this);
        this.formGetterType=this.formGetterType.bind(this);
        this.formGetterField=this.formGetterField.bind(this);

        this.indexChange=this.indexChange.bind(this);
        this.typeChange=this.typeChange.bind(this);
        this.formTirggerText=this.formTirggerText.bind(this);
        this.formGetterText=this.formGetterText.bind(this);
        this.handleAnalyze=this.handleAnalyze.bind(this);
        this.handleAnalyzeText=this.handleAnalyzeText.bind(this);
        this.changeDatatoObject=this.changeDatatoObject.bind(this);

    }

    getWordsList(){

    }

    componentDidMount() {
        this.fetchUsouIndexList();
    }

    fetchUsouIndexList() {
        let {index}=this.state;
        let data = new FormData();
        let type=this.props.page.type;
        let dic=this.props.page.dic;
        data.append("body", '{type:"'+type+'",dic:"'+dic+'",page:"'+(index-1)+'"}');
        fetch( contextUrl + "/usou/cluster/"+this.props.page.id+"/_analyze/word/list", {
            credentials: 'same-origin',
            method: 'POST',
            body:data
        } )
            .then( ( response ) => response.json())
            .then( ( responseData ) => {
                let worddata=[];
                if(type=='dic'){
                    worddata=this.changeDatatoObject(responseData.data.data);
                }else{
                    worddata=this.changeDatatoObject1(responseData.data.data);
                }
                this.setState({data: worddata,total:responseData.data.count});
            }).catch( ( err ) => console.log( err.toString()));

    }
    changeDatatoObject(data){
        if(data.length>0){
            let dataword=[];
            let result=[];
            for(let i=0;i<data.length;i++){
                dataword=data[i].split(",");
                let word={
                    word: dataword[0],
                    nature:dataword[1],
                    freq:dataword[2]
                }
                result.push(word);
            }
            return result;
            
        }else{
            return [];
        }

    }
    changeDatatoObject1(data){
         if(data.length>0){
            let result=[];
            for(let i=0;i<data.length;i++){
                let word={
                    word: data[i]
                }
                result.push(word);
            }
            return result;
            
        }else{
            return [];
        }
    }
    exchangePage(index){
        let {size,dataAll,searchBool}=this.state;
        let data=[];
        if(searchBool){
            for(let i = (index-1) * size; i < index * size; i++){
                if(dataAll[i]){
                    data.push(dataAll[i]);
                }
            }
            this.setState({data,index});
        }else{
            this.setState({index},()=>this.fetchUsouIndexList());
        }
    }

    handleClose() {
        this.resetWord();
        this.setState( { visible: false } );
    }

    uploadHandleClose(){
        this.setState( { uploadVisible: false } );
    }

    handleValue( ) {
        const data = this.getValue();
        let str = JSON.stringify( data, null, 2 );
        this.setState( {
            data: str
        } )

    }

    formTirgger( trigger ) {
        this.reset = trigger.reset;
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    formGetterAnalyzeText( getter ) {
        this.getValueAnalyzeText = getter.value;
    }

    formTirggerAnalyzeText( trigger ) {
        this.resetAnalyzeTex = trigger.reset;
    }

    addWord(){
        this.setState( { visible: true } );
    }

    noSelect(){
        popup(<Dialog
            title="是否选择"
            message="您没有选择，请重新选择"
            icon="warn"
        />);
    }

    alterWord(){
        this.setState( { alterVisible: true } );
    }
    uploadWord(){
        this.setState( { uploadVisible: true } );
    }

    confirmBatchDelete(){
        if (this.state.select==null||this.state.select==""){
            this.noSelect();
        }else {
            const approveBtn = {
                text: "确认",
                type: "primary",
                onClick: this.batchDelete
            };
            const cancelBtn = { text: "取消" }
            popup(<Dialog
                title="批量删除"
                message="确认批量删除"
                type="confirm"
                showCancelBtn
                cancelBtn={ cancelBtn }
                showApproveBtn
                approveBtn = { approveBtn }
            />);
        }

    }
    batchDelete( after ){
        let data = new FormData();
        let input = this.state.select;
        let type=this.props.page.type;
        let dic=this.props.page.dic;
        data.append("body", '{type:"'+type+'",dic:"'+dic+'",word:"'+input+'"}');
        let url=contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/word/batchdelete";
        fetch(url,{method:'post',body:data,credentials: 'same-origin',})
            .then((response)=>response.json())
            .then((responseData)=>{
                this.handleResponse(responseData,"批量删除");
            })
            .catch((err)=>console.log(err));
        after( true );
    }

    handleSelect( data ) {
        if(this.props.page.type=='synonyms'){
            this.setState( { select: data.map( ( key ) => key.word ).join('#')});
        }else{
            this.setState( { select: data.map( ( key ) => key.word ).join(',')});
        }
    }

    search(){
        let input = this.getValueInput();
        if(input == ''){
            this.setState({searchBool:false,index:1},()=>this.fetchUsouIndexList());
        }else {
            this.setState({ keywords : input,searchBool:true,index:1}, () => this.handleClick());
        }
    }
    handleClick() {
        let {keywords} = this.state;
        let type=this.props.page.type;
        let dic=this.props.page.dic;
        let data = new FormData();
        data.append("body", '{type:"'+type+'",dic:"'+dic+'",word:"'+keywords+'"}');
        const url = contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/word/search";
        fetch( url, {method:'post',body:data, credentials: 'same-origin'} )
            .then( ( response ) => response.json() )
            .then( ( searchData ) => {
                if(searchData.data.count==0){
                    popup( <Snackbar message="抱歉，暂时没有数据" /> );
                }
                let worddata=[];
                if(type=='dic'){
                    worddata=this.changeDatatoObject(searchData.data.data);
                }else{
                    worddata=this.changeDatatoObject1(searchData.data.data);
                }
                this.setState({dataAll: worddata,total:searchData.data.count},()=>this.exchangePage(1));
            })
            .catch( ( err ) => console.error( url, err.toString() ) );
    }

    handleInput( data ) {
        this.setState( { keyWords: data } );
    }
    addHandleSubmit(data1){
        let data={};
        let type=this.props.page.type;
        let dic=this.props.page.dic;
        data1["type"]=type;
        data1["dic"]=dic;
        let str = JSON.stringify(data1,null,2);
        //let body= "body";
        data['body'] = str;
        this.setState( { visible: false } );
        this.resetWord();
        return data;
    }

    formGetterAnalyzer(getter){
        this.getValueAnalyzer=getter.value;
    }

    formGetterIndex(getter){
        this.getValueIndex=getter.value;
    }

    formGetterType(getter){
        this.getValueType=getter.value;
    }
    formGetterField(getter){
        this.getValueField=getter.value;
    }
    handleResponse(data,tit){
        let status=data["status"];
        let message=data["message"];
        if(status=="200"){
            popup(<Dialog
                title={tit}
                message="操作成功"
            />);
            this.setState({index:1},()=>this.fetchUsouIndexList());
        }else if(status!="200"){
            popup(<Dialog
                title={tit}
                message="操作失败"
                icon="danger"
            />);
        }
    }

    formTirggerWord(trigger){
        this.resetWord=trigger.reset;
    }

    formGetterText(getter){
        this.getValueText=getter.value;
    }

    formTirggerText(trigger){
        this.resetText=trigger.reset;
    }

    formGetterInput(getter){
        this.getValueInput=getter.value;
    }

    handleAnalyze(){

        let index = this.getValueIndex();
        let type = this.getValueType();
        let field = this.getValueField();
        let text = this.getValueAnalyzeText();

        let data = new FormData();
        data.append("text",text);

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/"+index+"/"+field, {method:'post',credentials: 'same-origin',body:data})
            .then((response)=>response.json())
            .then((responseData)=>{this.setState({tokens:responseData.data.tokens});})
            .catch((err)=>console.log(err));
    }

    handleAnalyzeText(){

        let analyzer = this.getValueAnalyzer();
        let text = this.getValueText();
        let data = new FormData();
        data.append("text",text);
        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/"+analyzer, {method:'post',credentials: 'same-origin',body:data})
            .then((response)=>response.json())
            .then((responseData)=>{this.setState({tokensText:responseData.data.tokens});})
            .catch((err)=>console.log(err));
    }


    loadAnalyzerData(){

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/_cat/indices", {method:'get',credentials: 'same-origin'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let indices = this.parseIndex(responseData.data);
                let indexData=this.parseSource(indices);
                this.setState({indexData:indexData})})
            .catch((err)=>console.log(err));
    }

    parseIndex(data){
        let indices = new Array();
        for (let index in data){
            if (data[index].status=="open"){
                indices.push(data[index].index);
            }
        }
        return indices;
    }

    indexChange( dataString, isValid){

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/"+dataString+"/_mapping"
            ,{method:'post',credentials: 'same-origin'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let typesFields = this.parseMapping(responseData);
                let typeData=this.parseSource(typesFields.keys());
                this.setState({typesFields:typesFields});
                this.setState({typeData:typeData})})
            .catch((err)=>console.log(err));
    }

    parseMapping(responseData){
        let typesFields = new Map();
        for(let mapping in responseData){
            let mappings = responseData[mapping].mappings;
            for (let types in mappings){
                let properties = mappings[types];
                for (let field in properties){
                    typesFields.set(types,Object.keys(properties[field]));
                }
            }
        }
        return typesFields;
    }

    typeChange(dataString, isValid){
        let typesFields = this.state.typesFields;
        let fields = typesFields.get(dataString);
        let fieldData = this.parseSource(fields);
        this.setState({fieldData:fieldData});
    }

    parseSource(source){
        let array = new Array();
        for(let s of source){
            let temp={};
            temp["text"]=s;
            temp["value"]=s;
            array.push(temp);
        }
        return array;
    }

    confirmDelete(word){
        this.setState({deleteWord:word});
        const approveBtn = {
            text: "确认",
            type: "primary",
            onClick: this.deleteWord
        };
        const cancelBtn = { text: "取消" }
        popup(<Dialog
            title="删除词"
            message="确认删除"
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }

    deleteWord(after){
        let data = new FormData();
        let type=this.props.page.type;
        let dic=this.props.page.dic;
        let deleteWord=this.state.deleteWord;
        data.append("body", '{type:"'+type+'",dic:"'+dic+'",word:"'+deleteWord+'"}');
        let url=contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/word/delete"; 
            fetch(url,{method:'post',body:data,credentials: 'same-origin',})
            .then((response)=>response.json())
            .then((responseData)=>{
                this.handleResponse(responseData,"删除");
            })
            .catch((err)=>console.log(err));
        after( true );
    }

    handleKeyPress( event ) {
        if (event.which==13){
            this.search();
        }
    }
    wordSearchAuto(data){
        if(data == ''){
            this.setState({searchBool:false,index:1},()=>this.fetchUsouIndexList());
            //this.fetchUsouIndexList();
        }
    }
    searchAuto(){
        this.setState({searchBool:false,index:1},()=>this.fetchUsouIndexList());
        //this.fetchUsouIndexList();
    }

    beforeSubmit(data){
        if (data.file.length==0){
            popup(<Dialog  message="文件不能为空"/>);
        }else{
            this.setState({progress_bool : true});
            let str = '{"type":"'+this.props.page.type+'","dic":"'+this.props.page.dic+'"}';
            data['body'] = str;
            return data;
        }
    }

    formTirggerUpload( trigger ) {
        this.resetUpload = trigger.reset;
    }

    afterSubmit(data){
        if(data.status==200){
            popup(<Dialog  message="上传成功"/>);
            this.setState( { uploadVisible: false,progress_bool : false } );
            this.fetchUsouIndexList();
        }else {
            popup(<Dialog  message="上传失败"/>);
        }
        this.resetUpload();
    }

    render() {
        let {visible,uploadVisible,alterVisible,data,indexData,typeData,fieldData,tokens,tokensText,index,total,size,progress_bool} = this.state;

        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Divider/>
                <Row style={{minHeight: '600px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/dicManage'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{normal:20, small:24, medium:20, large:20}}>
                        <Row>
                            <NavigationBar code={'participleManage_Dictionary'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name} indices={this.props.page.dic}/>
                        </Row>
                            <Col size={{normal:24, small:24, medium:24, large:24}}>
                                    <Row style={{margin: '0px 0px 20px 0px'}}>
                                            <ButtonGroup style={{position: 'relative',zIndex: '1',float:'left'}}>
                                                <Button type="default" onClick={this.addWord} >添加词</Button>
                                                <Button type="default" onClick={this.uploadWord}>上传词</Button>
                                                <Button type="default" onClick={this.confirmBatchDelete}>批量删除</Button>
                                            </ButtonGroup>
                                         <Button type="primary" onClick={this.search.bind(this)} style={{float:'right'}}>搜索</Button>
                                        
                                         <Col size={6} style={{position: 'relative',zIndex: '1',float:'right'}}>
                                            <Input placeholder="请输入相关关键词，如 “中国”、“上海”等"
                                                    onKeyPress={ this.handleKeyPress.bind(this)} type={'search'}
                                                    getter={ this.formGetterInput }
                                                    onChange={this.wordSearchAuto.bind(this)}
                                                    onClear={this.searchAuto.bind(this)}/>
                                        </Col>
                                    </Row>
                                    <Row>
                                    {
                                        this.props.page.type=='dic'?
                                        <Table dataSource={ data } checkable onCheck={ this.handleSelect } striped={ true } multiLine={ true } >
                                            <Column title="序号" scaleWidth = '10%' textAlign="center">
                                                { ( value, index ) => { return Number(index)+1; } }
                                            </Column>
                                            <Column title="词名称" dataIndex="word" scaleWidth = '25%' textAlign="center"/>
                                            <Column title="词性" dataIndex="nature" scaleWidth = '25%' textAlign="center"/>
                                            <Column title="权重" dataIndex="freq"  scaleWidth = '25%' textAlign="center"/>
                                            <Column title="操作" scaleWidth = '15%' textAlign="center">
                                                { ( value) => {
                                                    return (
                                                        <div>
                                                            {/*<Button type="default" size="tiny" onClick={this.alterWord}>
                                                                <Icon type="edit" /> 修改
                                                            </Button>*/}
                                                            <Button type="default" size="tiny" onClick={this.confirmDelete.bind(this,value.word)}>
                                                                <Icon icon="trash-o" /> 删除
                                                            </Button>
                                                        </div>
                                                    )
                                                } }
                                            </Column>
                                        </Table>
                                        :
                                        <Table dataSource={ data } checkable onCheck={ this.handleSelect } striped={ true } multiLine={ true } >
                                            <Column title="序号" scaleWidth = '10%' textAlign="center">
                                                { ( value, index ) => { return Number(index)+1; } }
                                            </Column>
                                            <Column title="词名称" dataIndex="word" scaleWidth = '70%' textAlign="center"/>
                                            <Column title="操作" scaleWidth = '20%' textAlign="center">
                                                { ( value) => {
                                                    return (
                                                        <div>
                                                            {/*<Button type="default" size="tiny" onClick={this.alterWord}>
                                                                <Icon type="edit" /> 修改
                                                            </Button>*/}
                                                            <Button type="default" size="tiny" onClick={this.confirmDelete.bind(this,value.word)}>
                                                                <Icon icon="trash-o" /> 删除
                                                            </Button>
                                                        </div>
                                                    )
                                                } }
                                            </Column>
                                        </Table>
                                    }
                                    <Divider />
                                    </Row>
                                    <Pagination style={{marginBottom: '40px'}} name="pages"  index={ index }  total={ total>1000?1000:total }
                                                size={ 10 } align='right'
                                                onChange={this.exchangePage.bind(this)}/>
                            </Col>
                    </Col>
                </Row>
                <Form type="horizontal" 
                            async={ true }
                            action= {contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/word/add"}
                            method="post" 
                            onSubmit={this.addHandleSubmit}
                            onAfterSubmit={this.handleResponse} 
                            trigger={this.formTirggerWord}>
                        {
                            visible?
                            <Modal onClose={ this.handleClose.bind( this ) }  >
                                <ModalHeader>
                                    添加词
                                </ModalHeader>
                                <ModalBody>
                                        <FormItem  name={"word"} required={true}>
                                            <Label>词名称</Label>
                                            <Input name="word"/>
                                        </FormItem>
                                        {
                                            this.props.page.type=='dic'?
                                            <div>
                                            <FormItem name={"nature"}>
                                                <Label>词性</Label>
                                                <Select  name="nature" placeholder="Please select" multiple={ false } required={true}>
                                                    <Option value="n" >n</Option>
                                                    <Option value="v" >v</Option>
                                                    <Option value="nv" >nv</Option>
                                                    <Option value="a" >a</Option>
                                                    <Option value="adj" >adj</Option>
                                                </Select>
                                            </FormItem>

                                            <FormItem name={"freq"} required={true}>
                                                <Label>词频</Label>
                                                <Input name="freq"/>
                                            </FormItem>
                                            </div>
                                            :
                                            ''
                                        }
                                </ModalBody>
                                <ModalFooter>
                                    <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                                    <Button type="primary" htmlType="submit" >确定</Button>
                                </ModalFooter>
                            </Modal>
                            :
                            null
                        }
                </Form>
                <Modal visible={ alterVisible } onClose={ this.handleClose.bind( this ) }  >
                    <ModalHeader>
                        修改词
                    </ModalHeader>
                    <ModalBody height={ 200 }>

                        <Form type="horizontal" >
                            <FormItem name={"input"}>
                                <Label>词名称</Label>
                                <Input name="word"/>
                            </FormItem>

                            <FormItem name={"input"}>
                                <Label>词性</Label>
                                <Input name="natural"/>
                            </FormItem>

                            <FormItem name={"input"}>
                                <Label>词频</Label>
                                <Input name="freq"/>
                            </FormItem>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                        <Button type="primary" htmlType="submit">确定</Button>
                    </ModalFooter>
                </Modal>

                <Form type="horizontal"
                      action={contextUrl + "/usou/cluster/"+this.props.page.id+"/_analyze/word/upload"}
                      method="post"
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={ this.beforeSubmit.bind(this) }
                      onAfterSubmit={this.afterSubmit.bind(this)}
                      trigger={this.formTirggerUpload.bind(this)}
                >
                    <Modal visible={ uploadVisible } size="medium" onClose={ this.uploadHandleClose.bind( this ) }>
                        <ModalHeader>
                            上传词
                        </ModalHeader>
                        <ModalBody height={ 220 }>
                            <FormItem >
                                <Label>文件路径</Label>
                                <Upload placeholder="选择文件" name="file" limit={ 1 }  />
                            </FormItem>
                            <div style={{marginLeft:'30px'}}>
                                <Text color="skyblue">1.系统支持.txt格式文件进行上传</Text>
                                <Text color="skyblue">2.txt文件的<a href="/usou/resource/dic_example_file.txt">示例文件</a></Text>
                            </div>
                            {progress_bool ? <Progress type="loading" /> : ''}
                            {/*<Text color="skyblue">2.csv文件的示例文件</Text>*/}
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={ this.uploadHandleClose.bind( this ) }>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Footer/>
            </Page>
        );
    }
}
participleManage.UIPage = page;
export default participleManage;