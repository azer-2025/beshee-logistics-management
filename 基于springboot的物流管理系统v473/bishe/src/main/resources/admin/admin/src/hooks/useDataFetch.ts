import axios from 'axios';

export const useDataFetch = async () => {
  try {
    const response = await axios.get('/api');
    return response.data;
  } catch (error) {
    console.error('Error fetching data:', error);
    throw error;
  }
};
