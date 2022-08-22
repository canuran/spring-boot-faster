package canuran.common.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.mockito.configuration.DefaultMockitoConfiguration;
import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.internal.configuration.GlobalConfiguration;
import org.mockito.internal.util.MockUtil;
import org.mockito.internal.util.ObjectMethodsGuru;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.mock.MockName;
import org.mockito.stubbing.Answer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 单测基础类，使大部分Mock方法都返回有效的值。
 *
 * @author caiyouyuan
 * @since 2019年07月12日
 */
@SuppressWarnings("unchecked")
public class BaseMockitoTest {

    private static final ReturnsDefaultValues DEFAULT_VALUES = new ReturnsDefaultValues();
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void useDefaultReturn() {
        setMockitoConfiguration(new DefaultMockitoConfiguration() {
            @Override
            public Answer<Object> getDefaultAnswer() {
                return DEFAULT_VALUES;
            }
        });
    }

    @AfterClass
    public static void useEmptyReturn() {
        setMockitoConfiguration(new DefaultMockitoConfiguration());
    }

    protected static void setMockitoConfiguration(IMockitoConfiguration configuration) {
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        try {
            Field configurationField = GlobalConfiguration.class.getDeclaredField("GLOBAL_CONFIGURATION");
            configurationField.setAccessible(true);
            ThreadLocal<IMockitoConfiguration> threadLocal = (ThreadLocal<IMockitoConfiguration>) configurationField.get(globalConfiguration);
            threadLocal.set(configuration);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static class ReturnsDefaultValues implements Answer<Object>, Serializable {
        private static final long serialVersionUID = 1998191268711234347L;
        ObjectMethodsGuru methodsGuru = new ObjectMethodsGuru();
        MockUtil mockUtil = new MockUtil();

        public Object answer(InvocationOnMock invocation) {
            if (methodsGuru.isToString(invocation.getMethod())) {
                Object mock = invocation.getMock();
                MockName name = mockUtil.getMockName(mock);
                if (name.isDefault()) {
                    return "Mock for " + mockUtil.getMockSettings(mock).getTypeToMock().getSimpleName() + ", hashCode: " + mock.hashCode();
                } else {
                    return name.toString();
                }
            } else if (methodsGuru.isCompareToMethod(invocation.getMethod())) {
                return invocation.getMock() == invocation.getArguments()[0] ? 0 : 1;
            }
            Type returnType = invocation.getMethod().getGenericReturnType();
            return returnValueFor(returnType);
        }

        Object returnValueFor(Type type) {
            return BeanTestUtils.generateInstance(type);
        }
    }

}
