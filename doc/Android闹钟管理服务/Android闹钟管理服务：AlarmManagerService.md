# Android闹钟管理服务：AlarmManagerService

作者：[井方哥](https://github.com/zengjingfang)

校对：[井方哥](https://github.com/zengjingfang)

文章状态：编辑中



**文章目录**

+ 一、Alarm服务启动
+ 二、Alarm设置规则
+ 三、Alarm分发流程
+ 四、总结

# 一、Alarm服务启动

当机器开机启动的时候，会启动AlarmServiceManager,主要要做四个重要事情：

### 1、初始化mNativeData
 

		mNativeData = init();

		private native long init();

> 注意： 我们可以发现对于native层的设置都离不开这个mNativeData，下述5个方法也是闹钟的核心所在

 	private native void close(long nativeData);
    private native void set(long nativeData, int type, long seconds, long nanoseconds);
    private native int waitForAlarm(long nativeData);
    private native int setKernelTime(long nativeData, long millis);
    private native int setKernelTimezone(long nativeData, int minuteswest);

### 2、设置时区信息

+ 从本机的配置文件中获取时区信息
	
    		// We have to set current TimeZone info to kernel
     		// because kernel doesn't keep this after reboot
     		setTimeZoneImpl(SystemProperties.get(TIMEZONE_PROPERTY));
+ 换算成GMT标准
	
			// Update the kernel timezone information
            // Kernel tracks time offsets as 'minutes west of GMT'
            int gmtOffset = zone.getOffset(System.currentTimeMillis());
            setKernelTimezone(mNativeData, -(gmtOffset / 60000));

+ 设置到系统内核中
    
			private native int setKernelTimezone(long nativeData, int minuteswest);


### 3、设置当前系统时间

   +  当前时间小于开机时间则设置开机时间

	        if (mNativeData != 0) {
	            final long systemBuildTime = Environment.getRootDirectory().lastModified();
	            if (System.currentTimeMillis() < systemBuildTime) {
	                Slog.i(TAG, "Current time only " + System.currentTimeMillis()
	                        + ", advancing to build time " + systemBuildTime);
	                setKernelTime(mNativeData, systemBuildTime);
	            }
	        }
   +  设置到系统内核当中
			
			private native int setKernelTime(long nativeData, long millis);   


### 4、初始化ClockReceiver广播接收者

+ ACTION_ TIME_ TICK：系统时间广播，每隔一分钟触发一次
+ ACTION_ DATE_ CHANGED：设备日期发生改变时会发出此广播


#  二、Alarm设置规则


### 1、AlarmManagerServicce中的关键方法

#### AlarmManagerService#setImpl

	void setImpl(int type, long triggerAtTime, long windowLength, long interval,
	            PendingIntent operation, IAlarmListener directReceiver, String listenerTag,
	            int flags, WorkSource workSource, AlarmManager.AlarmClockInfo alarmClock,
	            int callingUid, String callingPackage) {
	
	        final long maxElapsed;
	        if (windowLength == AlarmManager.WINDOW_EXACT) {
	            maxElapsed = triggerElapsed;
	        } else if (windowLength < 0) {
	            maxElapsed = maxTriggerTime(nowElapsed, triggerElapsed, interval);
	            // Fix this window in place, so that as time approaches we don't collapse it.
	            windowLength = maxElapsed - triggerElapsed;
	        } else {
	            maxElapsed = triggerElapsed + windowLength;
	        }
	
	}


由上述代码可知，是为了计算maxElapsed,存在如下三种情况：

+ windowLength == AlarmMananger.WINDOW_EXACT: 直接返回为触发的时间，不做对齐处理。
+ windowLength< 0，进行计算：
     + interval == 0  ： triggerAtTime +0.75倍闹钟时间，若闹钟时间小于10秒，则就为triggerAtTime 
     + interval ！= 0 ： 直接返回triggerAtTime 


#### AlarmManagerService#maxTriggerTime

    // Apply a heuristic to { recurrence interval, futurity of the trigger time } to
    // calculate the end of our nominal delivery window for the alarm.
    static long maxTriggerTime(long now, long triggerAtTime, long interval) {
        // Current heuristic: batchable window is 75% of either the recurrence interval
        // [for a periodic alarm] or of the time from now to the desired delivery time,
        // with a minimum delay/interval of 10 seconds, under which we will simply not
        // defer the alarm.
        long futurity = (interval == 0)
                ? (triggerAtTime - now)
                : interval;
        if (futurity < MIN_FUZZABLE_INTERVAL) {
            futurity = 0;
        }
        return triggerAtTime + (long)(.75 * futurity);
    }

假如nowElapsed=1，triggerElapsed=2，interval=0。则返回 2+（0.75*（2-1））=2.75。这里实际就是增加了闹钟时间的0.75倍，比如说设置了10分钟的闹钟，那么增大7.5分钟。但是如果值小于10秒，则为0。

#### AlarmManagerService#setImplLocked

    private void setImplLocked(int type, long when, long whenElapsed, long windowLength,
            long maxWhen, long interval, PendingIntent operation, IAlarmListener directReceiver,
            String listenerTag, int flags, boolean doValidate, WorkSource workSource,
            AlarmManager.AlarmClockInfo alarmClock, int callingUid, String callingPackage) {
        // New一个Alarm实体对象
        Alarm a = new Alarm(type, when, whenElapsed, windowLength, maxWhen, interval,
                operation, directReceiver, listenerTag, workSource, flags, alarmClock,
                callingUid, callingPackage);
       // 省略代码
        removeLocked(operation, directReceiver);
      // 设置闹钟时间
        setImplLocked(a, false, doValidate);
    }

	private void setImplLocked(Alarm a, boolean rebatching, boolean doValidate) {
	        // 找有没有存在的batch可以存入本次的 alarm
	         int whichBatch = ((a.flags&AlarmManager.FLAG_STANDALONE) != 0)
	                ? -1 : attemptCoalesceLocked(a.whenElapsed, a.maxWhenElapsed);
	        if (whichBatch < 0) {
	          // 没有则new一个新的batch，并保存到mAlarmBatches里面去
	            Batch batch = new Batch(a);
	            addBatchLocked(mAlarmBatches, batch);
	        } else {
	            // 找到一个已存在的batch，并存入刷新mAlarmBatches
	            Batch batch = mAlarmBatches.get(whichBatch);
	            if (batch.add(a)) {
	                // The start time of this batch advanced, so batch ordering may
	                // have just been broken.  Move it to where it now belongs.
	                mAlarmBatches.remove(whichBatch);
	                addBatchLocked(mAlarmBatches, batch);
	            }
	        }
	      // 省略代码
	      rescheduleKernelAlarmsLocked();
	      updateNextAlarmClockLocked();
	}

#### AlarmManagerService#Batch

	// Batch 的构造方法
	Batch(Alarm seed) {
	            start = seed.whenElapsed;// Batch的起始点
	            end = seed.maxWhenElapsed;//Batch的结束点
	            flags = seed.flags;
	            alarms.add(seed);// 本Batch的alarms列表
	}
	
	// 根据Batch的边界条件判断该batch是否可以hold住传入的alarm
	boolean canHold(long whenElapsed, long maxWhen) {
	        // 新的alarm 必须要在旧的alarm的范围内，才能加入
	        return (end >= whenElapsed) && (start <= maxWhen);
	}
	
	// 给一个batch添加一个新的alarm，并且该batch根据新的alarm的边界取交集，作为batch新的边界
	        boolean add(Alarm alarm) {
	            boolean newStart = false;
	            // narrows the batch if necessary; presumes that canHold(alarm) is true
	            int index = Collections.binarySearch(alarms, alarm, sIncreasingTimeOrder);
	            if (index < 0) {
	                index = 0 - index - 1;
	            }
	            alarms.add(index, alarm);
	            if (DEBUG_BATCH) {
	                Slog.v(TAG, "Adding " + alarm + " to " + this);
	            }
	           // 其实点取大的
	            if (alarm.whenElapsed > start) {
	                start = alarm.whenElapsed;
	                newStart = true;
	            }
	           //结束点取小的
	            if (alarm.maxWhenElapsed < end) {
	                end = alarm.maxWhenElapsed;
	            }
	            flags |= alarm.flags;
	
	            if (DEBUG_BATCH) {
	                Slog.v(TAG, "    => now " + this);
	            }
	            return newStart;
	        }

#### AlarmManagerService.rescheduleKernelAlarmsLocked

    void rescheduleKernelAlarmsLocked() {
        // Schedule the next upcoming wakeup alarm.  If there is a deliverable batch
        // prior to that which contains no wakeups, we schedule that as well.
        long nextNonWakeup = 0;
        if (mAlarmBatches.size() > 0) {
           // 拿到第一个存在wakeup的batch,
            final Batch firstWakeup = findFirstWakeupBatchLocked();
          // 拿到第一个batch
            final Batch firstBatch = mAlarmBatches.get(0);
          // 如果存在wakeup的batch，则设置系统闹钟为第一个batch的时间
            if (firstWakeup != null && mNextWakeup != firstWakeup.start) {
                mNextWakeup = firstWakeup.start;
                mLastWakeupSet = SystemClock.elapsedRealtime();
               // 设置到系统，并这个type会唤醒系统
                setLocked(ELAPSED_REALTIME_WAKEUP, firstWakeup.start);
            }
            if (firstBatch != firstWakeup) {
                nextNonWakeup = firstBatch.start;
            }
        }
      // 对于不是wakeup的闹钟处理
        if (mPendingNonWakeupAlarms.size() > 0) {
            if (nextNonWakeup == 0 || mNextNonWakeupDeliveryTime < nextNonWakeup) {
                nextNonWakeup = mNextNonWakeupDeliveryTime;
            }
        }
        if (nextNonWakeup != 0 && mNextNonWakeup != nextNonWakeup) {
            mNextNonWakeup = nextNonWakeup;
            setLocked(ELAPSED_REALTIME, nextNonWakeup);
        }
    }

#### AlarmManagerService#set

	// jni到系统底层的闹钟服务
	private native void set(long nativeData, int type, long seconds, long nanoseconds);

####  AlarmManagerService.java

	  // duration： alarm设置的时间间隔，比如是10分钟后醒来的闹钟
	    static int fuzzForDuration(long duration) {
	        if (duration < 15*60*1000) {
	            // If the duration until the time is less than 15 minutes, the maximum fuzz
	            // is the duration.
	            return (int)duration;
	        } else if (duration < 90*60*1000) {
	            // If duration is less than 1 1/2 hours, the maximum fuzz is 15 minutes,
	            return 15*60*1000;
	        } else {
	            // Otherwise, we will fuzz by at most half an hour.
	            return 30*60*1000;
	        }
	    }
	 // 针对非wakeup的alarm的延时处理
	   long currentNonWakeupFuzzLocked(long nowELAPSED) {
	        long timeSinceOn = nowELAPSED - mNonInteractiveStartTime;
	        if (timeSinceOn < 5*60*1000) {
	            // If the screen has been off for 5 minutes, only delay by at most two minutes.
	            return 2*60*1000;
	        } else if (timeSinceOn < 30*60*1000) {
	            // If the screen has been off for 30 minutes, only delay by at most 15 minutes.
	            return 15*60*1000;
	        } else {
	            // Otherwise, we will delay by at most an hour.
	            return 60*60*1000;
	        }
	    }



### 2、AlarmManagerServicce中的对齐策略

![image](https://user-images.githubusercontent.com/11888369/37208459-70155b58-23dc-11e8-8ab2-1e7e74e73040.png)

+ 相关数据结构：
     + AlarmManagerService对应一个mAlarmBatches,包含了闹钟服务所有的Alarm Batch
     + 一个Batch包含了符合条件的所有Alarm
     + Alarm是一个闹钟的单元，内包含的信息主要由外部接口设定，比如:
          + int type:闹钟类型 ELAPSED_REALTIME、RTC、RTC_WAKEUP等
          + boolean wakeup:闹钟休眠后，如果要醒来，即type为XXX_WAKEUP
          + PendingIntent operation:闹钟触发醒来后的行为
          + long when:触发时间 UTC类型，绝对时间，System.currentTimeMillis()获取
          + long windowLength：浮动窗口，限制maxWhenElapsed到whenElapsed的范围
          + long whenElapsed:相对触发时间，SystemClock.elapsedRealtime()获取,也是Batch设置时的start时间
          + long maxWhenElapsed:最大触发时间，也是Batch设置时的end时间
          + long repeatInterval：时间间隔，用于重复的时间闹钟
+ 对齐设计思想：大致的方案就就是把相近的闹钟放到同一个batch里面批量处理。

+ Batch的规则：
   + 第一个Alarm:  假如设定了一个10min后的闹钟，则start1=当前时间+10，end1=1.75xstart1
   + Add一个Alarm:  如果alarm2的start和end时间在start1的区间内，则放到同一个batch。
   + 更新该Batch: 这个batch的start更新为start2。即这个batch的所有闹钟将在start2这个时间点醒来。

+ 逐步处理每个醒来的Batch。


### 3、AlarmManagerServicce与系统休眠相关的策略

+ 待续

#三、Alarm分发流程

#### 1、主要过程

+ 开启了一个AlarmThread，并进行死循环监听系统唤醒
+ waitForAlarm是个阻塞方法，到了设置的闹钟设定的时间便返回，并执行下一步动作
+ 遍历这个batch的alarm,发送alarm的PendingIntent

#### 2、源码解读

#### AlarmManagerService.AlarmThread


 	private class AlarmThread extends Thread {      
        public void run() {
            ArrayList<Alarm> triggerList = new ArrayList<Alarm>();
            //  死循环，一直监听系统的闹钟
            while (true)
            {
                // 系统层阻塞方法，等着闹钟醒来
                int result = waitForAlarm(mNativeData);
                mLastWakeup = SystemClock.elapsedRealtime();
               // 省略代码
                if (result != TIME_CHANGED_MASK) {
                        boolean hasWakeup = triggerAlarmsLocked(triggerList, nowELAPSED, nowRTC);
                        if (!hasWakeup && checkAllowNonWakeupDelayLocked(nowELAPSED)) {
                              // 省略代码
                        } else {
                            // now deliver the alarm intents; if there are pending non-wakeup
                            // alarms, we need to merge them in to the list.  note we don't
                            // just deliver them first because we generally want non-wakeup
                            // alarms delivered after wakeup alarms.
                            rescheduleKernelAlarmsLocked();
                            updateNextAlarmClockLocked();
                           // 省略代码
                          //  终于确定要分发这个batch所有的alarm了
                            deliverAlarmsLocked(triggerList, nowELAPSED);
                        }
                    }

                } else {
                    // Just in case -- even though no wakeup flag was set, make sure
                    // we have updated the kernel to the next alarm time.
                    synchronized (mLock) {
                        rescheduleKernelAlarmsLocked();
                    }
                }
            }
        }
    }
	

	void deliverAlarmsLocked(ArrayList<Alarm> triggerList, long nowELAPSED) {
        mLastAlarmDeliveryTime = nowELAPSED;
        for (int i=0; i<triggerList.size(); i++) {
            Alarm alarm = triggerList.get(i);
            // 分发alarm
           mDeliveryTracker.deliverLocked(alarm, nowELAPSED, allowWhileIdle);
           
        }
    }


#### AlarmManagerService#DeliveryTracker#deliverLocked

	public void deliverLocked(Alarm alarm, long nowELAPSED, boolean allowWhileIdle) {
	     // 发送alarm的PendingIntent
	    alarm.operation.send(getContext(), 0,
	    mBackgroundIntent.putExtra(
	    Intent.EXTRA_ALARM_COUNT, alarm.count),
	     mDeliveryTracker, mHandler, null,
	     allowWhileIdle ? mIdleOptions : null);
	    } 
	   // 最后要通知到ams
	   if (alarm.type == ELAPSED_REALTIME_WAKEUP|| alarm.type == RTC_WAKEUP) {
	         bs.numWakeup++;
	         fs.numWakeup++;
	         if (alarm.workSource != null && alarm.workSource.size() > 0) {
	              for (int wi=0; wi<alarm.workSource.size(); wi++) {
	                   final String wsName = alarm.workSource.getName(wi);
	                   ActivityManager.noteWakeupAlarm( alarm.operation, alarm.workSource.get(wi),
	                                (wsName != null) ? wsName : alarm.packageName,
	                                alarm.statsTag);
	                  }
	              } else {
	                  ActivityManager.noteWakeupAlarm( alarm.operation, alarm.uid, alarm.packageName, alarm.statsTag);
	              }
	       }              
	}

# 四、总结

对于Alarm的管理都是基于4个native方法进行的，setKernelTime、setKernelTimezone方法设置了时区和当前时间，声明了机器当前的时间状态。set方法设置闹钟要醒来的时间，另外开启一个AlarmThread线程并在死循环里监听waitForAlarm方法返回分发设置的alarm。其中的关键还是set的管理，针对Client的AlarmManager的接口配置的不同参数，我们实行了相关的策略，其中最主要的就是batch对齐策略。