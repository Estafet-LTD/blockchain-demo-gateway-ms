const Estacoin = artifacts.require("Estacoin");

module.exports = function(deployer) {
  deployer.deploy(Estacoin);
};
