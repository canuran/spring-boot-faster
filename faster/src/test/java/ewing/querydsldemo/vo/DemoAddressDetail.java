package ewing.querydsldemo.vo;

import ewing.querydsldemo.entity.DemoAddress;

import java.util.List;

/**
 * 带下级地址。
 */
public class DemoAddressDetail extends DemoAddress {

    private List<DemoAddress> subAddresses;

    public List<DemoAddress> getSubAddresses() {
        return subAddresses;
    }

    public void setSubAddresses(List<DemoAddress> subAddresses) {
        this.subAddresses = subAddresses;
    }

    // 实现equals和hashCode方便在集合中使用

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemoAddress)) return false;

        DemoAddress that = (DemoAddress) o;

        return getAddressId().equals(that.getAddressId());
    }

    @Override
    public int hashCode() {
        return getAddressId().hashCode();
    }
}
