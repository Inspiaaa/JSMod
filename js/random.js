
const Random = {
	random: () => Math.random(),

	biasedRandom: (min, max, bias, influence) => {
		// Random in range
		var rnd = Math.random() * (max - min) + min
		// Random mixer
		mix = Math.random() * influence
		// Mix full range and bias
    	return rnd * (1 - mix) + bias * mix
	},

	biasedRandint: (min, max, bias, influence) => Math.round(Random.biasedRandom(min, max, bias, influence)),

	randint: (min, max) => Math.round(Math.random() * (max - min) + min)
}
