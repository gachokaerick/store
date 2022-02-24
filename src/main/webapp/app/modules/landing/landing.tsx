import './landing.scss';
import { useAppSelector } from 'app/config/store';
import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { Select, Space } from 'antd';

export const Landing = () => {
  const account = useAppSelector(state => state.authentication.account);
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  });

  const { Option } = Select;

  return (
    <div>
      <section className={'landing-hero'}></section>
      <section className={'catalog-filters'}>
        <Space size={'small'} className={'h-100 container'}>
          <Select className="select-filter" mode="multiple" allowClear placeholder="Brand">
            <Option key="1">Brand1</Option>
            <Option key="2">Brand2</Option>
            <Option key="3">Brand3</Option>
          </Select>
          <Select className="select-filter" mode="multiple" allowClear placeholder="Type">
            <Option key="1">Type1</Option>
            <Option key="2">Type2</Option>
            <Option key="3">Type3</Option>
          </Select>
        </Space>
      </section>
    </div>
  );
};

export default Landing;
