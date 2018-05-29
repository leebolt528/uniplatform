import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,DateTime,Dialog,Checkbox,TreeSelect,Dropdown,Progress,D3Tree} from 'epm-ui';
import {ClassGroup} from './classGroup';

const contextUrl = '/uniplatform';

const page = {
    title: '分类体系管理',
    css: [
        '../css/index.min.css',
        '../css/classManage.min.css',
        '../css/leftnav.min.css',
        '../css/treechart.min.css'
    ]
};

class ClassManage extends Component{
     constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            classCount:[0],
            classGroup:[],
            classGroupEdit:[],
            showCount:1,
            objects:'',
            treeData:[],
            treeDataOut:[],
            classifyDataList:[],
            pageStart:0,
            searchWord:"",
            createBool:true,
            editShow:'',
            editShowTree:[],
            editShowObject:[],
            selectData:[],
            selectDataId:[],
            selectAllDataId:[],
            classifyId:"",
            showTreeBool:true,
            editId:"",
            delEditId:[],
            editClassifyId:"",
            editClassifyName:"",
            editNewDependences:"",
            editDeletedObjects:[],
            editUpdateObjects:[],
            editAddedObjects:[],
            uploadClassVisible:false,
            resultBool:true,
            classResetGroup:[],
            useClassify:[],
            delUseBool:false,
            progressBool:false,
            searchTableBool:false
        }
        this.formTirggerClass=this.formTirggerClass.bind(this);
        this.exchangePage=this.exchangePage.bind(this);
        this.getWordInput = this.getWordInput.bind(this);
      
    }

    componentDidMount() {
        this.fetchTreeDataList();
        this.fetchClassifyList();
       
    }
    //获取前置依赖二级树
    fetchTreeDataList(value){
         fetch(Uniplatform.context.url + '/nlap/admin/classifyMgmt/initClassify', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            let treeData=data.classifies;
            let treeDataDel=[];
            // if(value){
            //     treeData.map(function(dataSource,index){
            //         if(value.descendantClassify.indexOf(dataSource.data.id)>=0){
            //             dataSource.isLeaf=true;
            //         }
            //     });

            // }
            if(value){
                treeData.map(function(dataSource,index){
                    if(value.descendantClassify.indexOf(dataSource.data.id)==-1){
                        treeDataDel.push(dataSource);
                    }
                });
                treeData=treeDataDel;
            }
            this.setState({ treeData});
        }).catch((err) => console.log(err.toString()));
    }
    //获取分类规则表
    fetchClassifyList(){
        let {useClassify}=this.state;
        useClassify=[];
        let param = new FormData();
        param.append('pageSize',this.state.size);
        param.append('pageIndex',this.state.index);

        fetch(Uniplatform.context.url + '/nlap/admin/classifyMgmt/classifyInfo', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let classifyDataList=data.data;
            classifyDataList.map((value,index)=>{
                if(value.classify.used==true){
                    useClassify.push(value.classify.id);
                }
            });
            this.handleSelectFetch(classifyDataList);
            let total=data.totalNumber;
            let resultBool=false;
            this.setState({ classifyDataList,total,resultBool,useClassify});
        }).catch((err) => console.log(err.toString()));
    }
    //获取过滤分类规则
    fetchFileClassifyList(){
        let param = new FormData();
        param.append('pageSize',this.state.size);
        param.append('pageIndex',this.state.index);
        param.append('keyWord',this.state.searchWord);

        fetch(Uniplatform.context.url + '/nlap/admin/classifyMgmt/searchClassify', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let classifyDataList=data.data;
            let total=data.totalNumber;
            this.setState({ classifyDataList,total});
        }).catch((err) => console.log(err.toString()));
    }
    //获取组件input值
    classValue(data){
        let {classGroup,objects} = this.state;
        let keyID = data.classId;
        classGroup.map( (item,index) => {
            if(keyID == item.classId){
                classGroup.splice(index,1);
            }
        });

        classGroup.push(data);
        let objectsArr=[];
        Object.keys(classGroup).map((index) => {
            objectsArr.push(classGroup[index].className);
            objectsArr=[...new Set(objectsArr)];
        });
        objects=objectsArr.toString();
        this.setState({ classGroup,objects });
    }
    classValueEdit(data){
        let {classGroupEdit} = this.state;
        let keyID = data.classId;
        classGroupEdit.map( (item,index) => {
            if(keyID == item.classId){
                classGroupEdit.splice(index,1);
            }
        });

        classGroupEdit.push(data);
        this.setState({ classGroupEdit });
    }
    //添加单行下拉框
    addClass(){
        let {showCount,editShowObject}=this.state;
        editShowObject.push({name:"",editable:true});
        showCount++;
        let {classCount} = this.state;
        classCount.push(0);
        this.setState({ classCount,showCount,editShowObject});
    }
    //删除单行下拉框
    delClass(key){
        let {delEditId,classGroupEdit}=this.state;
        if(this.state.createBool==false){
            delEditId.push(key);
        }
        let {classCount,classGroup,showCount,objects}=this.state;
            document.getElementsByClassName(key)[0].style.display="none";
            showCount--;
        classGroup.map((item,index) => {
            if(key == item.classId){
                classGroup.splice(index,1);
            }
        });
        classGroupEdit.map((item,index) => {
            if(key == item.classId){
                classGroupEdit.splice(index,1);
            }
        });
        let objectsArr=[];
        Object.keys(classGroup).map((index) => {
            objectsArr.push(classGroup[index].className);
        });
        objectsArr=[...new Set(objectsArr)];
        objects=objectsArr.toString();
        this.setState({classGroup,showCount,delEditId,objects});
    }

    trim(s){
        return s.replace(/(^\s*)|(\s*$)/g, "");
    }
     //创建分类体系
    beforeAddClassifySubmit(data){
        let {objects,classGroup,classifyDataList}=this.state;
        let objectsArr=[];
        let objectsArrOne=[];
        classGroup.map((value)=>{
            value.className=this.trim(value.className);
        });
        Object.keys(classGroup).map((index) => {
            objectsArr.push(classGroup[index].className);
            objectsArrOne=[...new Set(objectsArr)];
        });
        if(objectsArr.length!=objectsArrOne.length){
            popup(<Snackbar message="该分类体系下存在同名对象！"/>) 
            return;
        }

        if(data.preClassifyAndObject!==""){
            data.preClassifyAndObject=data.preClassifyAndObject.join(';');
        }
        data.classifyName=this.trim(data.classifyName);
        if(data.classifyName==""){
             popup(<Snackbar message="请输入类别名称！"/>) 
             return;
         }
         data.objects=objects;
         if(objects==""){
             popup(<Snackbar message="请添加对象！"/>) 
             return;
         }
         this.setState({objects:""});
        return data;
    }
    afterAddClassifySubmit(data){
        this.closeAddClass();
        data.status == '200' ? popup(<Snackbar message={data.msg}/>) +this.createSuccess() : popup(<Snackbar
        message={data.msg}/>)
    }
    createSuccess(){
        let index=1;
        let pageStart=0;
        let size=10;
        let searchTableBool=false;
        this.resetSearchCorpus();
        this.setState({index,pageStart,size,searchTableBool},()=>{this.fetchClassifyList();this.fetchTreeDataList();});
    }
    //清空回显
    formTirggerClass( trigger ) {
        this.resetClass = trigger.reset;
    }
    searchTirggerCorpus(trigger){
        this.resetSearchCorpus = trigger.reset;
    }
    closeAddClass(){
        let {createBool,classCount,classGroup,showCount,classResetGroup}=this.state;
        classResetGroup.forEach(function (reset, sameElement, set) {
            reset();
        });
        this.resetClass();
        this.resetTree();
        createBool=true;
        classCount=[0];
        classGroup=[];
        showCount=1;
        document.getElementsByClassName(0)[0].style.display="inline-block";
        this.setState({createBool,classCount,classGroup,showCount});
    }
   
    exchangePage(index,size){
        let pageStart=index-1;
        this.setState({index,pageStart,size},()=>{this.state.searchTableBool?this.fetchFileClassifyList():this.fetchClassifyList()});
    }
    //根据类别名称查询分类
    getWordInput(getter){
        this.getWordValue=getter.value;
    }
    wordSearch( event ) {
        let index=1;
        if (event.which==13){
            let searchWord = this.getWordValue();
            searchWord=this.trim(searchWord);
            let size=10;
            let searchTableBool=true;
            this.setState({searchWord,index,size,searchTableBool}, () => this.fetchFileClassifyList());
        }
    }
    wordSearchAuto(data){
        if(data==""){
            let index=1;
            let size=10;
            let pageStart=0;
            this.setState({searchWord:"",index,size,pageStart}, () => this.fetchFileClassifyList());
        }
    }
    //编辑分类
    editClassModal(value){
        $(document).scrollTop(0);
        let {showCount,classCount,createBool,editShow,editShowTree,classGroup,editShowObject,editId,classGroupEdit,delEditId}=this.state;
        createBool=false;
        editShow=value;
        editShowTree=[];
        showCount=1;
        classCount=[0];
        classGroup=[];
        editId=value.classify.id;
        classGroupEdit=[];
        delEditId=[];
        Object.keys(value.pre).map((index) => {
           editShowTree.push({name:value.pre[index].preObjectName,value:value.pre[index].preObjectId});
        });
        editShowObject=[];
        value.object.map((data) => {
           editShowObject.push(data);
        });

        if(editShowObject.length>1){
           for(let i=1;i<editShowObject.length;i++){
                showCount++;
                classCount.push("0");
           }
        }
        this.fetchTreeDataList(value);
        this.setState({createBool,editShow,editShowTree,showCount,classCount,classGroup,editShowObject,editId,classGroupEdit,delEditId});
    }
    closeEditClass(){
        let {createBool,classCount,classGroup,showCount,classGroupEdit,delEditId}=this.state;
        createBool=true;
        classCount=[0],
        classGroup=[],
        showCount=1,
        classGroupEdit=[];
        delEditId=[];
        this.fetchTreeDataList();
        this.setState({createBool,classCount,classGroup,showCount,classGroupEdit,delEditId});
    }
    beforeEditClassifySubmit(dataSource){
        let data={};
        //分类体系ID
        let editClassifyId=this.state.editId;
        //新的分类体系名称
        let editClassifyName=this.trim(dataSource.classifyName);
        //前置依赖
        let editNewDependences="";
        if(dataSource.preClassifyAndObject!==""){
            editNewDependences=dataSource.preClassifyAndObject.join(';');
        }
        let editDeletedObjects=[];
        let editUpdateObjects=[];
        let editAddedObjects=[];
        //返回初始   分类体系对象
        let oldEditShowObject=this.state.editShowObject;
        //返回新的   分类体系对象
        let newClassGroupEdit=this.state.classGroupEdit;
        let objectsArr=[];
        let objectsArrOne=[];
        Object.keys(newClassGroupEdit).map((index) => {
            objectsArr.push(newClassGroupEdit[index].className);
            //objectsArrOne=[...new Set(objectsArr)];
        });
        Object.keys(oldEditShowObject).map((index) => {
            if(oldEditShowObject[index].name!==''){
                objectsArr.push(oldEditShowObject[index].name);
            }
        });
        objectsArrOne=[...new Set(objectsArr)];
        if(objectsArr.length!=objectsArrOne.length){
            popup(<Snackbar message="该分类体系下存在同名对象！"/>) 
            return;
        }
        //返回删除   分类体系对象
         let oldDelEditId=this.state.delEditId;
         oldDelEditId.map(function(index){
             newClassGroupEdit.map(function(item,index0){
                 if(item.classId==index){
                     newClassGroupEdit.splice(index0,1);
                 }
             });
         });
        for(let i=0;i<newClassGroupEdit.length;i++){
            if(newClassGroupEdit[i].className==""){
                popup(<Snackbar message="请删除空的对象名称！"/>) 
                return;
            }
        }
        //删除的分类对象
        oldDelEditId.map((index) => 
            {
                if(oldEditShowObject[index].hasOwnProperty('classifyId'))
                    {editDeletedObjects.push(oldEditShowObject[index].id);}
            }
        );
        if(editDeletedObjects!=[]){
            editDeletedObjects=editDeletedObjects.join(',');
        }
        //新增的分类对象
        let length=0;
        oldEditShowObject.map((data) => 
            {
                if(data.hasOwnProperty('classifyId'))
                    {length++;}
            }
        );
        newClassGroupEdit.map((data) => 
            {
                if(data.classId>=length)
                    {
                        editAddedObjects.push(data.className);
                    }
            }
        );
        if(editAddedObjects!=[]){
            editAddedObjects=editAddedObjects.join(',');
        }
        //更新的分类对象
        newClassGroupEdit.map((data) => 
            {
                if(data.classId<length){
                    editUpdateObjects.push(oldEditShowObject[data.classId].id+":"+data.className);
                }
            }
        );
        if(editUpdateObjects!=[]){
            editUpdateObjects=editUpdateObjects.join(';');
        }
        data.classifyName=editClassifyName;
        data.classifyId=editClassifyId;
        data.newDependences=editNewDependences;
        data.deletedObjects=editDeletedObjects;
        data.updateObjects=editUpdateObjects;
        data.addedObjects=editAddedObjects;
        let classGroupEdit=[];
        let delEditId=[];
        this.setState({classGroupEdit,delEditId});
        return data;
    }
    afterEditClassifySubmit(data){
         data.status == '200' ? popup(<Snackbar message={data.msg}/>) +this.exitSuccess() : popup(<Snackbar
        message={data.msg}/>)
    }
    exitSuccess(){
        if(this.state.searchTableBool){
            this.fetchFileClassifyList();
        }else{
            this.fetchClassifyList();
        }
        this.fetchTreeDataList();
        this.closeEditClass();
    }
    //选择表格数据
    handleSelect(data,currentData){
        let {selectDataId,selectData,selectAllDataId}=this.state;
        if(selectAllDataId.length==data.length){
            data.map((value)=>{
                selectDataId.push(value.classify.id);
            });
            selectDataId=[...new Set(selectDataId)];
        }else if(data.length==0){
            selectAllDataId.map((value1)=>{
                selectDataId.map((value2,index)=>{
                    if(value1==value2){
                         selectDataId.splice(index,1);
                    }
                });
            });
        }else{
            if(currentData){
                if(selectDataId.indexOf(currentData.classify.id)!==-1){
                    let index=selectDataId.indexOf(currentData.classify.id);
                    selectDataId.splice(index,1);
                }else{
                    selectDataId.push(currentData.classify.id);
                }
            }
        }
        selectData = selectDataId.join(',');
        this.setState({selectData,selectDataId});
        // let selectData = [];
        // selectData = Object.keys(data).map((index) => 
        //     `${data[index].classify.id}`
        // ).join(',');
        // this.setState({selectData});
    }
     handleSelectFetch(classifyDataList){
        let {selectDataId,selectAllDataId}=this.state;
        selectAllDataId=[];
        if(selectDataId){
                selectDataId.map((value1)=>{
                    classifyDataList.map((value2)=>{
                        if(value1==(value2.classify.id)){
                            value2.checked=true;
                        }
                    });
                });
            };
            if(classifyDataList){
                classifyDataList.map((value)=>{
                    selectAllDataId.push(value.classify.id);
                });
            };
        this.setState({selectAllDataId});
    }
     //删除分类体系
    delSelectData(){
        let {selectData,useClassify,delUseBool}=this.state;
         if(selectData == ""){
            popup( <Snackbar message={`没有选择要删除的数据`} /> );
            return false; 
        } 
        selectData=selectData.split(',');
        useClassify.map((value1)=>{
            selectData.map((value2,index)=>{
                if(value1==value2){
                    delUseBool=true;
                    selectData.splice(index,1);
                }
            });
        });
        this.setState({delUseBool});
        if(selectData.length==0){
            popup( <Snackbar message={`正在使用的分类体系不允许删除！`} /> );
            return false; 
        } 
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleClassApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除所选分类体系？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
        this.setState({selectData});
    }
    //确认批量删除分类体系
    handleClassApprove(after){
        let {selectData,total,size,index,delUseBool}=this.state;
        let param = new FormData();
        param.append("classifyId", selectData);
        fetch( Uniplatform.context.url + '/nlap/admin/classifyMgmt/deleteClassify', {
            credentials: 'same-origin',
            method: 'POST',
            body : param
            })
            .then((res) => res.json())
            .then((data) => {
                let index11=Math.ceil((total-selectData.length)/size);
                if(index11<index){
                    this.setState({index:1,pageStart:0});
                }else{
                    this.setState({index});
                }
                this.setState({selectData:'',selectDataId:[]});
                data.status == '200' ? `${delUseBool?popup(<Snackbar message={"使用的分类体系禁止删除,其余删除成功！"}/>):popup(<Snackbar message={data.msg}/>)}` + this.delSelectSuccess(): popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
            });
             after( true );
    }
    delSelectSuccess(){
        this.fetchTreeDataList();
        this.setState({delUseBool:false},()=>{this.state.searchTableBool?this.fetchFileClassifyList():this.fetchClassifyList()});
    }
    //删除分类体系
    delClassPrompt(value){
        let classifyId=value.classify.id;
        this.setState({classifyId});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleClassOneApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此分类体系？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确认删除分类体系
    handleClassOneApprove(after){
        let {classifyId,total,size,index}=this.state;
        let param = new FormData();
            param.append('classifyId',classifyId);
         fetch(Uniplatform.context.url + '/nlap/admin/classifyMgmt/deleteClassify', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                if(total%size==1&&index==Math.ceil(total/size) ){
                    this.setState({index:1,pageStart:0});
                }
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchClassifyList()+this.fetchTreeDataList() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
         after( true );
    }
    //上传规则
    formTirggerUpload( trigger ) {
        this.resetUpload = trigger.reset;
    }
    uploadClassModal(){
        this.setState({ uploadClassVisible: true,progressBool:false });
    }
    closeUploadClass(){
        this.resetUpload();
        this.setState({ uploadClassVisible: false });
    }
    beforeUploadSubmit(data){
        if(this.state.progressBool==false){
            if(data.file.length==0){
                popup(<Snackbar message="请选择需要上传的文件！"/>) 
                return;
            }
            this.setState({progressBool:true});
            return data;
        }
    }
    afterUploadSubmit(data){
        data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.uploadSuccess() : popup(<Snackbar
        message={data.msg}/>)+this.uploadFail()
    }
    uploadSuccess(){
        let index=1;
        let pageStart=0;
        let size=10;
        let searchTableBool=false;
        this.resetUpload();
        this.resetSearchCorpus();
        this.setState({uploadClassVisible: false,index,pageStart,size,searchTableBool},()=>{this.fetchClassifyList();this.fetchTreeDataList();});
    }
    uploadFail(){
        this.setState({progressBool: false});
    }
     //批量导出规则
    getJsonByIdDown(){
        let {selectData} = this.state;
        if(selectData == ""){
            popup( <Snackbar message={`没有选择要导出的数据`} /> );
        }else{
            this.clearSelectData();
            window.open(`${Uniplatform.context.url}/nlap/admin/classifyMgmt/exportToXML?classifyId=${selectData}`,'_self');
        } 
    }
    //清空表格选中数据及状态
    clearSelectData(){
        let {classifyDataList} = this.state;
        classifyDataList.map((value)=>{
            value.checked=false;
        });
        this.setState({selectData:'',selectDataId:[],classifyDataList});
    }
    //导出规则
    getJsonOneDown(value){
        let classifyId=value.classify.id;
        window.open(`${Uniplatform.context.url}/nlap/admin/classifyMgmt/exportToXML?classifyId=${classifyId}`,'_self');
    }
    //生成树形图
    treeClassModal(value){
        $(document).scrollTop(362);
        let showTreeBool=false;
        let classifyId=value.classify.id;
        let param = new FormData();
            param.append('classifyId',classifyId);
        fetch(Uniplatform.context.url + '/nlap/admin/classifyMgmt/generateTree', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                let treeDataOut=data.tree;
                this.setState({showTreeBool,treeDataOut});
                data.status == '200' ? popup() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
    }
    hideTreeClass(){
        let showTreeBool=true;
        this.setState({showTreeBool});
    }
    //TreeSelect单选
    handleBeforeSelect(node, currentSelectedNodes){
        // 只有第二个层级单选

        if ( node.level === 2 ) {
        if ( node.selected ) {
            return true;
        }

        let oldSelectedNode = null;
        currentSelectedNodes.forEach(selectedNode => {
            if (node.parent === selectedNode.parent) {
            oldSelectedNode = selectedNode;
            }
        });

        if ( oldSelectedNode ) {
            this.selectNode( ( _node ) => { return _node.key === oldSelectedNode.key }, false, false );
            return true;
        } else {
            return true;
        }

        } else {
        return true;
        }
    }
    displayNameFormatter( node ) {
        return node.parent ? `${node.parent.name}/${node.name}` : node.name;
    }
    render(){
        let {classCount,showCount,treeData,classifyDataList,pageStart,size,index,total,createBool,editShow,editShowTree,editShowObject,showTreeBool,treeDataOut,uploadClassVisible,resultBool,classResetGroup,progressBool}=this.state;
        return(
            <Page>
                {/*<COMM_HeadBanner prefix=" " />
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px',margin:'0px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'910px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <NLPMenu url={'/nlap/platform/classManage'}/>
                    </Col>*/}
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row>
                            {
                                createBool==true?
                                <div>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <span className="filter_span">创建分类</span>
                                    </Col>
                                    <div className="classManage_content">
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="contentPa">
                                            <Form 
                                                method="post"
                                                action={contextUrl + '/nlap/admin/classifyMgmt/addClassify'}
                                                async={true}
                                                type="inline"
                                                onSubmit={this.beforeAddClassifySubmit.bind(this)}
                                                onAfterSubmit={this.afterAddClassifySubmit.bind(this)}
                                                >
                                                    <Col size={{normal: 24, small: 24, medium: 24, large: 24}}>
                                                        <FormItem>
                                                            <Label>类别名称：</Label>
                                                            <Input placeholder="请输入名称" trigger={ this.formTirggerClass } style={{width:'500px'}} name="classifyName"/>
                                                        </FormItem>
                                                    </Col>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24}} >
                                                    <FormItem>
                                                            <Label>前置依赖：</Label>
                                                            <TreeSelect
                                                                name="preClassifyAndObject"
                                                                style={{width:'500px'}}
                                                                dataSource={ treeData } 
                                                                dataValueMapper={ 'id' }
                                                                checkable={ false }
                                                                multiple={true}
                                                                beforeSelect={ this.handleBeforeSelect.bind( this ) }
                                                                displayNameFormatter={ this.displayNameFormatter.bind( this ) }
                                                                capture={ 'childrenOnly' }
                                                                placeholder="请选择前置依赖"
                                                                trigger={ ( trigger ) => {
                                                                    this.resetTree = trigger.reset;
                                                                    this.selectNode = trigger.selectNode;
                                                                } }
                                                            />
                                                        </FormItem>
                                                    </Col>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                        <FormItem>
                                                            <Label style={{verticalAlign:`${showCount>3?"top":"middle"}`}}>类别对象：</Label>
                                                            <div style={{display:"inline-block",maxWidth:"830px"}}>
                                                                {
                                                                    classCount.map((item,index)=>{
                                                                        return (
                                                                            <div key={index} className={index} style={{display: `${index%4==3?'inline-block':"inline-block"}`}}>
                                                                                <ClassGroup trigger={ (t) => {classResetGroup.push(t) } } outIndex={index} getClassGroup={this.classValue.bind(this)}/>
                                                                                {
                                                                                    // index==0?
                                                                                    // <Button type="default" style={{display:`${showCount==1?"none":"inline-block"}`}} disabled onClick={this.delClass.bind(this,index)}><Icon icon="minus"/></Button>
                                                                                    // :
                                                                                    <Button type="default" style={{display:`${showCount==1?"none":"inline-block"}`}} onClick={this.delClass.bind(this,index)}><Icon icon="minus"/></Button>
                                                                                }
                                                                            </div>
                                                                        )
                                                                    })
                                                                } 
                                                                <Button style={{marginLeft: '5px',display:`${classCount.length==1?"inline-block":"inline-block"}`}} type="default" onClick={this.addClass.bind(this)}><Icon icon="plus" /></Button>
                                                            </div>
                                                        </FormItem>
                                                    </Col>
                                                    <Button type="default" htmlType="submit" style={{marginLeft:'50%'}}>确认创建</Button>
                                                    <Button type="default" onClick={this.closeAddClass.bind(this)}>取消</Button>
                                            </Form>
                                        </Col>
                                    </div>
                                </div>
                                :
                                <div>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <span className="filter_span">编辑分类</span>
                                    </Col>
                                    <div className="classManage_content" style={{border:"1px solid red"}}>
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="contentPa">
                                            <Form 
                                                method="post"
                                                action={contextUrl + '/nlap/admin/classifyMgmt/editClassify'}
                                                async={true}
                                                type="inline"
                                                onSubmit={this.beforeEditClassifySubmit.bind(this)}
                                                onAfterSubmit={this.afterEditClassifySubmit.bind(this)}
                                                >
                                                    <Col size={{normal: 24, small: 24, medium: 24, large: 24}}>
                                                        <FormItem>
                                                            <Label>类别名称：</Label>
                                                            <Input placeholder="请输入名称" style={{width:'500px'}} name="classifyName" value={editShow.classify.name}/>
                                                        </FormItem>
                                                    </Col>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24}} >
                                                    <FormItem>
                                                            <Label>前置依赖：</Label>
                                                            <TreeSelect
                                                                name="preClassifyAndObject"
                                                                style={{width:'500px'}}
                                                                dataSource={ treeData } 
                                                                dataValueMapper={ 'id' }
                                                                checkable={ false }
                                                                multiple={true}
                                                                capture={ 'childrenOnly' }
                                                                placeholder="请选择前置依赖"
                                                                value={ editShowTree }
                                                                beforeSelect={ this.handleBeforeSelect.bind( this ) }
                                                                displayNameFormatter={ this.displayNameFormatter.bind( this ) }
                                                            />
                                                        </FormItem>
                                                    </Col>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                                        <FormItem>
                                                            <Label style={{verticalAlign:`${showCount>3?"top":"middle"}`}}>类别对象：</Label>
                                                            <div style={{display:"inline-block",maxWidth:"830px"}}>
                                                                {
                                                                    classCount.map((item,index)=>{
                                                                        return (
                                                                            <div key={index} className={index} style={{display: `${index%4==3?'inline-block':"inline-block"}`}}>
                                                                                <ClassGroup outIndex={index} editShowObject={editShowObject} getClassGroup={this.classValueEdit.bind(this)}/>
                                                                                {
                                                                                    editShowObject[index].editable==true?
                                                                                    <Button type="default" style={{display:`${showCount==1?"none":"inline-block"}`}} onClick={this.delClass.bind(this,index)}><Icon icon="minus"/></Button>
                                                                                    :
                                                                                    <Button type="default" style={{display:`${showCount==1?"none":"inline-block"}`}} onClick={this.delClass.bind(this,index)} disabled><Icon icon="minus"/></Button>
                                                                                }
                                                                            </div>
                                                                        )
                                                                    })
                                                                } 
                                                                <Button style={{marginLeft: '5px',display:`${classCount.length==1?"inline-block":"inline-block"}`}} type="default" onClick={this.addClass.bind(this)}><Icon icon="plus" /></Button>
                                                            </div>
                                                        </FormItem>
                                                    </Col>
                                                    <Button type="default" htmlType="submit" style={{marginLeft:'50%'}}>确认提交</Button>
                                                    <Button type="default" onClick={this.closeEditClass.bind(this)}>返回</Button>
                                            </Form>
                                        </Col>
                                    </div>
                                </div>
                            }
                        </Row>
                        <Row>
                            {
                                showTreeBool?
                                <div>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <Input placeholder="请输入要查询的词" type="search"
                                            style={{float:'left'}}
                                            onKeyPress={ this.wordSearch.bind(this)}
                                            getter={ this.getWordInput }
                                            onChange={this.wordSearchAuto.bind(this)}
                                            trigger={ this.searchTirggerCorpus.bind(this) }>
                                            <Input.Left icon="search" />
                                        </Input>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.delSelectData.bind(this)} >
                                            <i className="fa fa-trash"></i> 批量删除
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.getJsonByIdDown.bind(this)}>
                                            <i className="fa fa-download"></i> 导出分类
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadClassModal.bind(this)}>
                                            <i className="fa fa-upload"></i> 上传分类
                                        </Button>
                                    </Col>
                                    {
                                        total==0&&resultBool==false?
                                        <div className="classManage_content" style={{textAlign:"center"}}>
                                            <span className="nullContent">不存在对应的体系</span>
                                        </div>
                                        :
                                        <div className="classManage_content">
                                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="contentPa">
                                                <Table dataSource={ classifyDataList } headBolder style={{color:'#54698d'}} checkable={ true } striped={true} className="table_head" onCheck={ this.handleSelect.bind(this) }>
                                                    <Column title="编号" scaleWidth = '7%' textAlign="center">
                                                        { ( value, index ) => { return Number(`${Number(this.state.pageStart)*Number(this.state.size)+Number(index)}`) + 1; } }
                                                    </Column>
                                                    <Column title="名称" scaleWidth = '12%'textAlign="center">
                                                        { ( value, index ) => { return value.classify.name }}
                                                    </Column>
                                                    <Column title="内容"  scaleWidth = '20%'textAlign="center">
                                                        { ( value, index ) => { return Object.keys(value.object).map((item) => { return value.object[item].name}).join(',')}}
                                                    </Column>
                                                    <Column title="创建者" scaleWidth = '10%'textAlign="center">
                                                        { ( value, index ) => { return value.classify.createUser }}
                                                    </Column>
                                                    <Column title="状态"  scaleWidth = '8%'textAlign="center">
                                                        { ( value, index ) => { 
                                                            
                                                            const a = <Dropdown position={'right'} key={index}>
                                                                    <Dropdown.Trigger action="hover">
                                                                        <span>{value.classify.used?"已使用":"未使用"}</span>
                                                                    </Dropdown.Trigger>
                                                                    <Dropdown.Content>
                                                                    {
                                                                        value.classify.used?
                                                                        <div style={ { maxWidth: '240px',maxHeight:'200px',overflowY:'auto'} }>
                                                                            {
                                                                                value.corpusSet.map((value,index)=>{
                                                                                    return (<p key={index} style={{marginLeft:'20px',marginRight:'20px'}}>{value}</p>);
                                                                                })
                                                                            }
                                                                        </div>
                                                                        :
                                                                        <div style={ { width: '200px'} }>
                                                                        {
                                                                            <p style={{marginLeft:'20px'}}>无任何语料集使用</p>
                                                                        }
                                                                        </div>
                                                                    }
                                                                    </Dropdown.Content>
                                                                </Dropdown>
                                                            return a;
                                                         }}
                                                    </Column>
                                                    <Column title="创建时间"  scaleWidth = '17%'textAlign="center">
                                                        { ( value, index ) => { return <DateTime format='yyyy/MM/dd hh:mm:ss' content={value.classify.createTime} style={{textAlign:'center'}}/> } }
                                                    </Column>
                                                    <Column title="操作" scaleWidth = '26%' textAlign="center">
                                                        { ( value ) => {
                                                            return (
                                                                <div style={{display: "inline-flex"}}>
                                                                     {
                                                                        value.classify.used==false?
                                                                        <Button type="default" size="tiny" onClick={this.editClassModal.bind(this,value)}>
                                                                            编辑
                                                                        </Button>
                                                                        :
                                                                        <Button type="default" size="tiny" disabled>
                                                                            编辑
                                                                        </Button>
                                                                    }
                                                                    {
                                                                        value.classify.used==false?
                                                                        <Button type="default" size="tiny" onClick={this.delClassPrompt.bind(this,value)}>
                                                                            删除
                                                                        </Button>
                                                                        :
                                                                        <Button type="default" size="tiny" disabled>
                                                                            删除
                                                                        </Button>
                                                                    }
                                                                    <Button type="default" size="tiny" onClick={this.getJsonOneDown.bind(this,value)}>
                                                                         导出
                                                                    </Button>
                                                                    {
                                                                        <Button type="default" size="tiny" onClick={this.treeClassModal.bind(this,value)}>
                                                                             树形图
                                                                        </Button>
                                                                    }
                                                                </div>
                                                            )
                                                        } }
                                                    </Column>
                                                </Table>
                                                <Divider />
                                                <Pagination index={ index }  total={total} size={ size } align='center' onChange={this.exchangePage} showDataSizePicker dataSizePickerList={['10','20','50','100']}/>
                                            </Col>
                                        </div>
                                    }
                                </div>
                                :
                                <div>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <Button type="default" className="button_noborder" style={{float:'right'}} onClick={this.hideTreeClass.bind(this)}><img src="../image/rule_return.svg" style={{width:'17px',height:'17px',marginRight:'5px',display:'inline-block'}}/>返回</Button>
                                    </Col>
                                    <div className="classManage_content" style={{minHeight:'700px'}}>
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="contentPa">
                                            <D3Tree
                                                dataSource={ treeDataOut }
                                                zoom={ true }
                                            />
                                        </Col>
                                    </div>
                                </div>
                            }
                        </Row>
                    </Col>
                </Row>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/classifyMgmt/importXML'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeUploadSubmit.bind(this)}
                          onAfterSubmit={this.afterUploadSubmit.bind(this)}
                    >
                    <Modal visible={ uploadClassVisible } size="middle" onClose={ this.closeUploadClass.bind( this ) }>
                        <ModalHeader>
                            上传分类
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>上传文件</Label>
                                <Upload placeholder="选择文件" name="file" multiple trigger={ this.formTirggerUpload.bind(this) }/>
                            </FormItem>
                            {
                                progressBool?
                                <Progress type="loading" />
                                :
                                null
                            }
                            <span className="upExamples"> 1、 系统支持.xml 格式的文本文件进行上传。<br/>
                                    2、xml文件的<a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=classify.xml">示例文件</a><br/>
                            </span>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeUploadClass.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                {/*<Footer />*/}
            </Page>
        );
    };
}

ClassManage.UIPage = page;
export default ClassManage;