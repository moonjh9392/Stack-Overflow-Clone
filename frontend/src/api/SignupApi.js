import instance from './root';

//dev : signup
//real : users/signup
//test : data.json 에 "signup": {} 추가
export const Signup = async (data) => {
	console.log(instance);
	try {
		//test용
		// data.accessToken = 'dd';
		// const result = await instance.post(`/signup`, data);
		console.log('Signup start');
		//real
		const result = await instance.post(`/users/signup`, data);
		console.log('Signup', result);
		return result.data;
	} catch (err) {
		console.log(err);
		return err;
	}
};
