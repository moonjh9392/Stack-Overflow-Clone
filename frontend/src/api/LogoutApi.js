import instance from './root';

//test
//test logout은 아무 의미 없음 통신 후처리를 위해 만듬
export const Logout = async (data) => {
	try {
		//test
		// data = {};
		// const result = await instance.post(`/login`, data);
		console.log('Logout', data);
		//real
		const result = await instance.delete(`/users/logout`);
		return result.data;
	} catch (err) {
		console.log(err);
		return err;
	}
};
