package wekb

import wekb.auth.User

class CuratoryGroupUser implements Serializable{

	User user
    CuratoryGroup curatoryGroup

	Date dateCreated
	Date lastUpdated

	static constraints = {
		dateCreated(nullable:true, blank:true)
		lastUpdated(nullable:true, blank:true)
	}

	static mapping = {
		id composite: ['curatoryGroup', 'user']
		version false
		user column: 'cgu_user_fk'
		curatoryGroup column: 'cgu_curatory_group_fk'
		dateCreated column:'cgu_date_created'
		lastUpdated column:'cgu_last_updated'
	}


	static CuratoryGroupUser get(long userId, long curatoryGroupId) {
		find 'from CuratoryGroupUser where user.id = :userId and curatoryGroup.id = :curatoryGroupId',
			[userId: userId, roleId: curatoryGroupId]
	}

	static CuratoryGroupUser create(User user, CuratoryGroup curatoryGroup, boolean flush = false) {
		new CuratoryGroupUser(user: user, curatoryGroup: curatoryGroup).save(flush: flush, insert: true)
	}

	static boolean remove(User user, CuratoryGroup curatoryGroup, boolean flush = false) {
		CuratoryGroupUser instance = CuratoryGroupUser.findByUserAndCuratoryGroup(user, curatoryGroup)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

}
