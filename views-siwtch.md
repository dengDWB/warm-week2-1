# 视图跳转方式，及注意事项 #

## Activity的4种加载模式 ##

在android的多activity开发中，activity之间的跳转可能需要有多种方式，有时是普通的生成一个新实例，有时希望跳转到原来某个activity实例，而不是生成大量的重复的activity。加载模式便是决定以哪种方式启动一个跳转到原来某个Activity实例。

在android里，有4种activity的启动模式，分别为：


standard: 标准模式，一调用startActivity()方法就会产生一个新的实例。

singleTop: 来了intent, 每次都创建新的实例，仅一个例外：当栈顶的activity 恰恰就是该activity的实例（即需要创建的实例)时，不再创建新实例。这解决了栈顶复用问题

singleTask: 来了intent后，检查栈中是否存在该activity的实例，如果存在就把intent发送给它，否则就创建一个新的该activity的实例，放入一个新的task栈的栈底。肯定位于一个task的栈底，而且栈中只能有它一个该activity实例，但允许其他activity加入该栈。解决了在一个task中共享一个activity。

singleInstance: 这个跟singleTask基本上是一样，只有一个区别：在这个模式下的Activity实例所处的task中，只能有这个activity实例，不能有其他的实例。一旦该模式的activity的实例已经存在于某个栈中，任何应用在激活该activity时都会重用该栈中的实例，解决了多个task共享一个activity。

这些启动模式可以在功能清单文件AndroidManifest.xml中进行设置，中的launchMode属性。

影响加载模式的一些特性：

核心的Intent Flag有：

FLAG_ACTIVITY_NEW_TASK 

FLAG_ACTIVITY_CLEAR_TOP
 
FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 

FLAG_ACTIVITY_SINGLE_TOP 

核心的特性有： 
taskAffinity 

launchMode 

allowTaskReparenting 

clearTaskOnLaunch 

alwaysRetainTaskState
 
finishOnTaskLaunch

Activity在Activity栈（Task）中的加载顺序是可以控制的，这就需要用到Intent Flag

## Intent常用标识 ##

**FLAG_ACTIVITY_BROUGHT_TO_FRONT** 

这个标志一般不是由程序代码设置的，如在launchMode中设置singleTask模式时系统帮你设定。

**FLAG_ACTIVITY_CLEAR_TOP**

如果设置，并且这个Activity已经在当前的Task中运行，因此，不再是重新启动一个这个Activity的实例，而是在这个Activity上方的所有Activity都将关闭，然后这个Intent会作为一个新的Intent投递到老的Activity（现在位于顶端）中。

例如，假设一个Task中包含这些Activity：A，B，C，D。如果D调用了startActivity()，并且包含一个指向Activity B的Intent，那么，C和D都将结束，然后B接收到这个Intent，因此，目前stack的状况是：A，B。

上例中正在运行的Activity B既可以在onNewIntent()中接收到这个新的Intent，也可以把自己关闭然后重新启动来接收这个Intent。如果它的启动模式声明为 “multiple”(默认值)，并且你没有在这个Intent中设置FLAG_ACTIVITY_SINGLE_TOP标志，那么它将关闭然后重新创建；对于其它的启动模式，或者在这个Intent中设置FLAG_ACTIVITY_SINGLE_TOP标志，都将把这个Intent投递到当前这个实例的onNewIntent()中。

这个启动模式还可以与FLAG_ACTIVITY_NEW_TASK结合起来使用：用于启动一个Task中的根Activity，它会把那个Task中任何运行的实例带入前台，然后清除它直到根Activity。这非常有用，例如，当从Notification Manager处启动一个Activity。

**FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET**

如果设置，这将在Task的Activity stack中设置一个还原点，当Task恢复时，需要清理Activity。也就是说，下一次Task带着 FLAG_ACTIVITY_RESET_TASK_IF_NEEDED标记进入前台时（典型的操作是用户在主画面重启它），这个Activity和它之上的都将关闭，以至于用户不能再返回到它们，但是可以回到之前的Activity。

这在你的程序有分割点的时候很有用。例如，一个e-mail应用程序可能有一个操作是查看一个附件，需要启动图片浏览Activity来显示。这个 Activity应该作为e-mail应用程序Task的一部分，因为这是用户在这个Task中触发的操作。然而，当用户离开这个Task，然后从主画面选择e-mail app，我们可能希望回到查看的会话中，但不是查看图片附件，因为这让人困惑。通过在启动图片浏览时设定这个标志，浏览及其它启动的Activity在下次用户返回到mail程序时都将全部清除。

**FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS**

如果设置，新的Activity不会在最近启动的Activity的列表中保存。

**FLAG_ACTIVITY_FORWARD_RESULT**

如果设置，并且这个Intent用于从一个存在的Activity启动一个新的Activity，那么，这个作为答复目标的Activity将会传到这个新的Activity中。这种方式下，新的Activity可以调用setResult(int)，并且这个结果值将发送给那个作为答复目标的 Activity。

**FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY**

这个标志一般不由应用程序代码设置，如果这个Activity是从历史记录里启动的（常按HOME键），那么，系统会帮你设定。

**FLAG_ACTIVITY_MULTIPLE_TASK**

不要使用这个标志，除非你自己实现了应用程序启动器。与FLAG_ACTIVITY_NEW_TASK结合起来使用，可以禁用把已存的Task送入前台的行为。当设置时，新的Task总是会启动来处理Intent，而不管这是是否已经有一个Task可以处理相同的事情。

由于默认的系统不包含图形Task管理功能，因此，你不应该使用这个标志，除非你提供给用户一种方式可以返回到已经启动的Task。

如果FLAG_ACTIVITY_NEW_TASK标志没有设置，这个标志被忽略。

**FLAG_ACTIVITY_NEW_TASK**

如果设置，这个Activity会成为历史stack中一个新Task的开始。一个Task（从启动它的Activity到下一个Task中的 Activity）定义了用户可以迁移的Activity原子组。Task可以移动到前台和后台；在某个特定Task中的所有Activity总是保持相同的次序。

这个标志一般用于呈现“启动”类型的行为：它们提供用户一系列可以单独完成的事情，与启动它们的Activity完全无关。

使用这个标志，如果正在启动的Activity的Task已经在运行的话，那么，新的Activity将不会启动；代替的，当前Task会简单的移入前台。参考FLAG_ACTIVITY_MULTIPLE_TASK标志，可以禁用这一行为。

这个标志不能用于调用方对已经启动的Activity请求结果。

**FLAG_ACTIVITY_NO_ANIMATION**

如果在Intent中设置，并传递给Context.startActivity()的话，这个标志将阻止系统进入下一个Activity时应用 Acitivity迁移动画。这并不意味着动画将永不运行——如果另一个Activity在启动显示之前，没有指定这个标志，那么，动画将被应用。这个标志可以很好的用于执行一连串的操作，而动画被看作是更高一级的事件的驱动。

**FLAG_ACTIVITY_NO_HISTORY** 

如果设置，新的Activity将不再历史stack中保留。用户一离开它，这个Activity就关闭了。这也可以通过设置noHistory特性。

**FLAG_ACTIVITY_NO_USER_ACTION**
 
如果设置，作为新启动的Activity进入前台时，这个标志将在Activity暂停之前阻止从最前方的Activity回调的onUserLeaveHint()。

典型的，一个Activity可以依赖这个回调指明显式的用户动作引起的Activity移出后台。这个回调在Activity的生命周期中标记一个合适的点，并关闭一些Notification。

如果一个Activity通过非用户驱动的事件，如来电或闹钟，启动的，这个标志也应该传递给Context.startActivity，保证暂停的Activity不认为用户已经知晓其Notification。

**FLAG_ACTIVITY_PREVIOUS_IS_TOP** 

如果设置，并且该活动是无论是在一个新的任务被启动或使顶端现有任务，那么它将被推出作为任务的前门。这将导致在具有在适当的状态下任务所需的任何亲和力的应用（或者移动的活动或从它），或者如果需要简单地重置该任务到其初始状态。

**FLAG_ACTIVITY_REORDER_TO_FRONT**

如果在Intent中设置，并传递给Context.startActivity()，这个标志将引发已经运行的Activity移动到历史stack的顶端。

例如，假设一个Task由四个Activity组成：A,B,C,D。如果D调用startActivity()来启动Activity B，那么，B会移动到历史stack的顶端，现在的次序变成A,C,D,B。如果FLAG_ACTIVITY_CLEAR_TOP标志也设置的话，那么这个标志将被忽略。

**FLAG_ACTIVITY_RESET_TASK_IF_NEEDED**

如果设置，并且该活动是无论是在一个新的任务被启动或使顶端现有任务，那么它将被推出作为任务的前门。这将导致在具有在适当的状态下任务所需的任何亲和力的应用（或者移动的活动或从它），或者如果需要简单地重置该任务到其初始状态。

**FLAG_ACTIVITY_SINGLE_TOP**’

如果设置，当这个Activity位于历史stack的顶端运行时，不再启动一个新的。
 
注意：如果是从BroadcastReceiver启动一个新的Activity，或者是从Service往一个Activity跳转时，不要忘记添加Intent的Flag为FLAG_ACTIVITY_NEW_TASK。