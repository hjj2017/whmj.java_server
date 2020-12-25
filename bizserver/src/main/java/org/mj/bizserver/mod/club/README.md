README
====

- 亲友圈彻底把 "成员中心" 和 "管理控制台" 分成两个功能!
- 成员中心 MemberCenter;
- 管理控制台 AdminCtrl;
- 这样做的目的是可以各自独立增减功能;
- 另外, AdminCtrl 可以调用 MemberCenter 中的功能, 但是反过来不可以!, 
  也就是说 MemberCenter 不可以调用 AdminCtrl 中的功能;
    ( 从用例上来说这样是合理的, 因为 Admin ( Actor ) 继承自 Member ( Actor ))