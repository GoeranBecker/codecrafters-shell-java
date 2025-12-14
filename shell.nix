{ pkgs ? import <nixpkgs> {} }:

let
  shellName = "CodeCrafters: Shell";

    unstable = import (fetchTarball
      "https://github.com/NixOS/nixpkgs/archive/nixos-unstable.tar.gz"
    ) {};

  devDependencies = {
    jdk24 = unstable.jdk25;
    mvn = pkgs.maven; 
  };
in
pkgs.mkShell {
  name = shellName;

  buildInputs = builtins.attrValues devDependencies;

  shellHook = ''
    echo "Dev-Environment for ${shellName}"

    export JAVA_HOME="${devDependencies.jdk24}"

    ${builtins.concatStringsSep "\n" (
      map (name: ''
        echo "${name}: ${devDependencies.${name}.version}"
      '') (builtins.attrNames devDependencies)
    )}
  '';
}
