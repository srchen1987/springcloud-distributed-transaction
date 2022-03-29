import org.aopalliance.intercept.MethodInvocation;

import com.pttl.distributed.transaction.context.DistributedTransactionContext;

public class JamesInvoker implements com.pttl.distributed.transaction.aspect.TransactionInterceptInvoker{

	@Override
	public Object invoke(MethodInvocation invocation, DistributedTransactionContext tc) throws Throwable {
		// TODO Auto-generated method stub
		Object obj = invocation.proceed();
		if(obj instanceof JamesResponseMessage) {
			JamesResponseMessage jm = (JamesResponseMessage) obj;
			if(	jm.getCode()==500) {
				tc.setCancel(true);
			}
		}
		return obj;
	}

}
