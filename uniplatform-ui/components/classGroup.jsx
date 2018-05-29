import React, { Component } from "react";
import { Page, Button, Input,Icon } from "epm-ui";

class ClassGroup extends Component{
  constructor(props) {
    super( props );
    this.state = {
      className : '',
      classField : {}
    }
  }
  componentWillMount() {
   
  }
  
  componentDidMount() {
    if( this.props.trigger ) {

      this.props.trigger( this.reset )
    }
  }
  changeGroup(dataString){
      let className = dataString;
      this.setState({ className }, ()=> this.fieldClassGroup());
  }  
  
  //返回数组
  fieldClassGroup(){
      let {className,classField} = this.state;
      classField["classId"] = this.props.outIndex;
      classField["className"] = className;
      this.props.getClassGroup(classField);
  }

  render(){
    let {editShowObject,outIndex}=this.props;
    return(
        <div style={{display:"inline-block"}}>
        {
          editShowObject?
          <div>
            {
              editShowObject[outIndex].editable==true?
              <Input name="objects" placeholder="请输入名称" value={editShowObject[outIndex].name} onChange={this.changeGroup.bind(this)}/>
              :
              <Input name="objects" placeholder="请输入名称" value={editShowObject[outIndex].name} disabled/>
            }
          </div> 
          : 
          <Input name="objects" trigger={ (t) => { this.reset = t.reset} } placeholder="请输入名称" onChange={this.changeGroup.bind(this)}/>
        }
      </div>

    )
  }
}

export default ClassGroup;