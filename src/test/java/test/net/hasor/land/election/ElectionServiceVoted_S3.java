//package test.net.hasor.land.election;
//import net.hasor.land.election.CollectVoteData;
//import net.hasor.rsf.RsfResult;
///**
// * 投票给 Server3
// * Created by yongchun.zyc on 2017/2/27.
// */
//public class ElectionServiceVoted_S3 extends AbstractElectionServiceVoted {
//    //
//    @Override
//    public RsfResult requestVote(CollectVoteData voteData) {
//        if ("server_3".equalsIgnoreCase(voteData.getServerID()) && testTerm(voteData.getTerm())) {
//            // 投票给 C
//            return voteOK(voteData.getServerID());
//        } else {
//            // 只能投 C
//            return voteFailed(voteData.getServerID());
//        }
//    }
//}