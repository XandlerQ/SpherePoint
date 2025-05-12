# SpherePoint

**SpherePoint** is a Java + Processing application for determining the optimal distribution of points on the surface of a sphere, aiming to maximize the separation between them.

## Target Function Formulations

The project includes two mathematical formulations for evaluating point configurations, both with gradient calculations:

- **Sum of Inverse Distances**: The sum of 1 / R_ij for all i ≠ j, where R_ij is the distance between points i and j.
- **Negative Minimum Distance**: The target function is the negated value of the minimum distance between any two points. This leads to optimal results when minimized.

![image](https://github.com/user-attachments/assets/3a145a57-8c6c-4c7d-a99d-94c9a434e558)


## Solver Methods

Two optimization algorithms are implemented:

- **Pattern Search (Hooke-Jeeves method)**
- **Optimal Gradient Descent** with a substep that performs a line search along the gradient direction.

## Visualization

The optimization process can be visualized in real-time or post-completion using the Processing graphics library.

## Results

**5 Points – Two Optimal Configurations**

![opt51](https://github.com/user-attachments/assets/86edf55d-60b3-4d03-9845-1e20ac34523d)  
![opt52](https://github.com/user-attachments/assets/01591dc8-c87e-4a89-b473-bcd127d9737b)

**20 Points – Regular Dodecahedron**

![opt20](https://github.com/user-attachments/assets/0994c7a0-c4b9-4932-8c9f-3c52cc131144)

### UML Scheme

![image](https://github.com/user-attachments/assets/537119e8-36df-4105-a176-58531bc90d84)
