import React, { Component } from 'react';
import {Select,Option,Input,FormItem} from 'epm-ui';

class QueryRange extends Component {
  
  constructor( props ) {
    super( props );
    
    this.state= {
      min:'gt',
      minInput: '',
      max:'lt',
      maxInput: '',
      range: {}
    }

    this.getQuery = this.getQuery.bind(this);
  } 

  getQuery(){
    let range = {};
    let { min, minInput,max,maxInput } = this.state;
    if(minInput != ''){
      range[min] = minInput;
    }
    if(maxInput != ''){
      range[max] = maxInput;
    }
    this.setState({ range });

    this.props.getQueryRange(range);
  }

  changeMin(data){
    let min = data;
    this.setState({ min },() => this. getQuery());
  }

  changeMax(data){
    let max = data;
    this.setState({ max },() => this. getQuery());
  }

  getMin(option,data){
      let minInput = data;
      this.setState({ minInput },() => this. getQuery());
  }

  getMax(option,data){
      let maxInput = data;
      this.setState({ maxInput },() => this. getQuery());
  }


  render() {
    let {minInput,maxInput} = this.state;
    return (
      <div className="selectconfig">
        <div style={{display: 'inline-block'}}>
          <Select value={ 'gt' } onChange={this.changeMin.bind(this)}>
              <Option value="gt">gt</Option>
              <Option value="gte">gte</Option>
          </Select>
        </div>
        <div style={{display: 'inline-block'}}>
          <Input type="text" value = {minInput} onChange={this.getMin.bind(this,'minInp')}/>
        </div>
        <div style={{display: 'inline-block'}}>
          <Select value={ 'lt' } onChange={this.changeMax.bind(this)}>
            <Option value="lt">lt</Option>
            <Option value="lte">lte</Option>
          </Select>
        </div>
        <div style={{display: 'inline-block'}}>
          <Input type="text" value={maxInput} onChange={this.getMax.bind(this,'maxInp')}/>
        </div>
      </div>
    );
  }

}

export default QueryRange;
export {QueryRange};