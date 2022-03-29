package com.pttl.distributed.transaction.timers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pttl.distributed.transaction.annotation.TransactionStatus;
import com.pttl.distributed.transaction.context.DistributedTransactionContext;
import com.pttl.distributed.transaction.message.MessageSender;
import com.pttl.distributed.transaction.repository.TransactionRepository;
import com.pttl.distributed.transaction.util.JsonUtils;
/**
 * 
 * @ClassName:  CompensationTimer   
 * @Description:   补偿定时器
 * @author: srchen    
 * @date:   2019年11月02日 上午3:13:26
 */
@Component
public class CompensationTimer {
	private static Logger log = LoggerFactory.getLogger(CompensationTimer.class);
	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	MessageSender messageSender;

	
	 @Scheduled(fixedRate=15000)
    private void configureTasks() {
		 try {
			 List<DistributedTransactionContext> list = transactionRepository.findALLBySecondsLater(60*3);
			 for(DistributedTransactionContext dc : list) {
				  String commiting = TransactionStatus.COMMITING;
				  if(commiting.equals(dc.getStatus()))continue;
					Map data = new HashMap();
					data.put("status",dc.getStatus());
					data.put("action",dc.getAction());
					data.put("globalTxId",dc.getGlobalTxId()); 
					String msg = JsonUtils.objectToJson(data);
					if(log.isDebugEnabled()) {
						log.debug("transaction compensate:{}",msg);
					}
					messageSender.sent(msg);
			    }
		} catch (Exception e) {
			log.error("",e);
		}
	 	}

}
