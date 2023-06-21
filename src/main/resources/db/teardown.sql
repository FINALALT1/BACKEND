-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table frequent_question_tb;
truncate table notice_tb;
truncate table board_tb;
truncate table board_bookmark_tb;
truncate table reply_tb;
truncate table rereply_tb;
truncate table award_tb;
truncate table branch_tb;
truncate table career_tb;
truncate table company_tb;
truncate table pb_tb;
truncate table pb_agreement_tb;
truncate table portfolio_tb;
truncate table reservation_tb;
truncate table review_tb;
truncate table style_tb;
truncate table user_tb;
truncate table user_agreement_tb;
truncate table user_bookmark_tb;
-- 모든 제약 조건 활성화
SET REFERENTIAL_INTEGRITY TRUE;