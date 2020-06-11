package kr.ac.konkuk.ccslab.cm.entity;

import java.net.InetSocketAddress;
import java.nio.channels.MembershipKey;
import java.util.LinkedList;
import java.util.Queue;

public class CMGroup extends CMGroupInfo {
    private CMMember m_groupUsers;
    private CMChannelInfo<InetSocketAddress> m_mcInfo;
    private MembershipKey m_membershipKey;    // required for leaving a group

    private Queue<CMUser> userQueue;
    private int currentIndex;
    private CMUser currentUser;
    private CMUser groupAdmin;

    public CMGroup() {
        super();
        m_groupUsers = new CMMember();
        m_mcInfo = new CMChannelInfo<InetSocketAddress>();
        m_membershipKey = null;
        currentIndex = -2;
        currentUser = null;
        groupAdmin = null;
    }

    public CMGroup(String strGroupName, String strAddress, int nPort) {
        super(strGroupName, strAddress, nPort);
        m_groupUsers = new CMMember();
        m_mcInfo = new CMChannelInfo<InetSocketAddress>();
        m_membershipKey = null;
        currentIndex = -2;
        currentUser = null;
        groupAdmin = null;
    }

    // set/get methods
    public synchronized CMMember getGroupUsers() {
        return m_groupUsers;
    }

    public synchronized CMChannelInfo<InetSocketAddress> getMulticastChannelInfo() {
        return m_mcInfo;
    }

    public synchronized MembershipKey getMembershipKey() {
        return m_membershipKey;
    }

    public synchronized void setMembershipKey(MembershipKey key) {
        m_membershipKey = key;
    }

    public synchronized void init() {
        userQueue = new LinkedList<CMUser>(m_groupUsers.getAllMembers());
    }

    public synchronized boolean canGameProceed() {
        return 1 < userQueue.size();
    }

    public synchronized void refreshQueue() {
        int size = userQueue.size();
        while (size-- > 0) {
            CMUser user = userQueue.poll();
            if (!m_groupUsers.isMember(user) || user.getLife() <= 0) {
                continue;
            }
            userQueue.add(user);
        }
    }

    public synchronized CMUser getCurrentUser() {
        return currentUser;
    }

    public synchronized CMUser getNextUser() {
        CMUser nextUser = null;
        do {
            nextUser = userQueue.poll();
        } while (!userQueue.isEmpty() && nextUser != null && (!m_groupUsers.isMember(nextUser) || nextUser.getLife() <= 0));

        if (nextUser != null && m_groupUsers.isMember(nextUser)) {
            userQueue.add(nextUser);
        }
        return nextUser;
    }

    public synchronized CMUser getGroupAdmin() {
        return groupAdmin;
    }

    public synchronized void setGroupAdmin(CMUser user) {
        this.groupAdmin = user;
        user.setAdmin(true);
    }

    public synchronized void finishGame() {
        userQueue.clear();
        currentIndex = -2;
        currentUser = null;
    }
}
