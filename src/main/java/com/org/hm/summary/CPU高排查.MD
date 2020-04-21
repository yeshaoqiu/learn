[TOC]

###1、病号是谁？（WHO）
第一步：采用 top 命令，找出 CPU 占用最高的病号 PID ；

第二步：通过 ps -ef | grep PID 查看病号对应的真实身份。

###2、病号哪里出了问题？（WHERE）
第一步：采用 jstack -l PID >> PID.log 获取病号的各器官信息的病历单；

第二步：采用 top -Hp PID 拿到占用 CPU 最高的器官昵称 PID ;

第三步：采用 printf "%x\n" PID 根据器官昵称 PID 的拿到器官真实身份 TID ;

第四步：采用 grep TID -A20 pid.log 根据 TID 去病历单中匹配，确定是哪出了问题。

###3、捉得病根、便可拿出医药箱，对症下药啦。（HOW）
