package ewing.application.common;

import java.io.Serializable;

/**
 * 命名值接口。
 *
 * @author Ewing
 * @since 2018年05月15日
 */
public interface NameValue {

    default String name() {
        return toString();
    }

    <E extends Serializable> E value();

    class Context {
        private String name;

        private Serializable value;

        public Context(NameValue nv) {
            this.name = nv.name();
            this.value = nv.value();
        }

        public String getName() {
            return name;
        }

        public Serializable getValue() {
            return value;
        }
    }

}
