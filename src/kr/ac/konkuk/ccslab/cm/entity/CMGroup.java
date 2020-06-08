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
    private int leftUserNum;

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
        leftUserNum = m_groupUsers.getMemberNum();
        userQueue = new LinkedList<CMUser>(m_groupUsers.getAllMembers());
    }

    public synchronized boolean canGameProceed() {
        return 1 < userQueue.size();
    }

    public synchronized CMUser getCurrentUser() {
        return currentUser;
    }

    public synchronized CMUser getNextUser() {
        /*
        if (m_groupUsers.isEmpty()) {
            currentIndex = -2;
            currentUser = null;
        } else if (currentIndex == -2 && !m_groupUsers.isEmpty()) {
            currentIndex = 0;
            currentUser = m_groupUsers.getUser(0);
        } else if (m_groupUsers.isMember(currentUser)) {
            do {
                currentIndex = (currentIndex + 1) % (m_groupUsers.getAllMembers().size());
                currentUser = m_groupUsers.getUser(currentIndex);
            } while (currentUser.getLife() <= 0);
        } else if (!m_groupUsers.isMember(currentUser)) {
            if (currentIndex >= m_groupUsers.getMemberNum()) {
                currentIndex = 0;
            }
            currentUser = m_groupUsers.getUser(currentIndex);
            while (currentUser.getLife() <= 0) {
                currentIndex = (currentIndex + 1) % (m_groupUsers.getAllMembers().size());
                currentUser = m_groupUsers.getUser(currentIndex);
            }
        }
        return currentUser;

         */
        CMUser nextUser = null;
        while (!userQueue.isEmpty() && (nextUser == null || !m_groupUsers.isMember(nextUser))) {
            if (nextUser != null && !m_groupUsers.isMember(nextUser)) {
                m_groupUsers.removeMember(nextUser);
            }
            nextUser = userQueue.poll();
        }
        if (userQueue.isEmpty()) {
            nextUser = null;
        }
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

    public synchronized void decreaseLeftUserNum() {
        if (0 < leftUserNum) {
            leftUserNum--;
        }
    }

    public synchronized int getLeftUserNum() {
        return leftUserNum;
    }

    public synchronized void finishGame() {
        userQueue.clear();
        currentIndex = -2;
        currentUser = null;
        leftUserNum = 0;
    }
}
